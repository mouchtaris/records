package gv
package isi
package junix
package leon
package mirror

import
  akka.{
    NotUsed,
  },
  akka.stream.{
    FlowShape,
    Materializer,
  },
  akka.stream.scaladsl.{
    GraphDSL,
    Broadcast,
    Zip,
    Flow,
    Sink,
    MergeHub,
    Keep,
  },
  akka.http.scaladsl.model.{
    Uri,
    HttpRequest,
    HttpResponse,
    HttpEntity,
    ContentTypes,
    StatusCodes,
  },
  akka.event.{
    LoggingAdapter,
  }

trait MirrorHttpRequestHandling
  extends AnyRef
  with leon.MirrorHttpRequestHandling
{
  protected[this] val packageSource: PackageSource
  protected[this] val keyMaker: MirrorRequestToPackageSourceKey
  protected[this] val missingPackageHandler: MissingPackageHandler
  protected[this] val log: LoggingAdapter
  protected[this] implicit val materializer: Materializer

  object privates {
    import missingPackageHandler.Input
    val missing: Sink[Input, NotUsed] =
      MergeHub
        .source[Input]
        .viaMat(missingPackageHandler.flow)(Keep.left)
        .toMat(Sink.ignore)(Keep.left)
        .run()
  }

  final def apply(mirror: leon.Mirror): Flow[HttpRequest, HttpResponse, NotUsed] = {

    object func {
      val toPath: HttpRequest ⇒ Uri.Path =
        _.uri.path

      val toKey: Uri.Path ⇒ PackageSource.Key.T =
        keyMaker(mirror, _)

      val packageSourceResult: PackageSource.Key.T ⇒ PackageSource.Result =
        packageSource.apply

      val packageResultToHttpResponse: PartialFunction[PackageSource.Result, HttpResponse] = {
        case PackageSource.Results.Found(key, source) ⇒
          log.info(s"Is found under $key")
          val entity = HttpEntity(
            data = source,
            contentType = ContentTypes.`application/octet-stream`
          )
          HttpResponse(
            entity = entity
          )
        case PackageSource.Results.Later(key) ⇒
          log debug s"Try later for $key"
          HttpResponse(
            status = StatusCodes.ServiceUnavailable
          )
        case PackageSource.Results.Failed(key) ⇒
          log debug s"found failed under $key"
          HttpResponse(
            status = StatusCodes.NotFound
          )
        case PackageSource.Results.Missing(key, _) ⇒
          log debug s"missing under $key"
          packageResultToHttpResponse(PackageSource.Results.Later(key))
      }
    }

    object flows {

      val toPath = Flow fromFunction func.toPath
      val applyPackageSource: Flow[Uri.Path, PackageSource.Result, NotUsed] =
        Flow[Uri.Path]
          .via(Flow fromFunction func.toKey)
          .via(Flow fromFunction func.packageSourceResult)

      val missingResultToMissingHandlerInput: Flow[(Uri.Path, PackageSource.Result), missingPackageHandler.Input, NotUsed] =
        Flow[(Uri.Path, PackageSource.Result)]
          .collect {
            case (path, PackageSource.Results.Missing(_, sink)) ⇒
              (mirror, path, sink)
          }

      val missing =
        missingResultToMissingHandlerInput
          .to(privates.missing)

      val toResponse = Flow fromFunction func.packageResultToHttpResponse

      lazy val process: Flow[HttpRequest, HttpResponse, NotUsed] = Flow fromGraph GraphDSL.create() { implicit b ⇒ import GraphDSL.Implicits._
        val toPath = b add flows.toPath
        val bcastPath = b add Broadcast[Uri.Path](2)
        val bcastPs = b add Broadcast[PackageSource.Result](2)
        val missing = b add flows.missing
        val zip = b add Zip[Uri.Path, PackageSource.Result]()
        val toResponse = b add flows.toResponse

        toPath ~> bcastPath ~> applyPackageSource ~> bcastPs ~> toResponse
                  bcastPath ~> zip.in0;              bcastPs ~> zip.in1

        zip.out ~> missing

        FlowShape(toPath.in, toResponse.out)
      }


    }

    flows.process
  }

}
