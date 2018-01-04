package gv
package isi
package junix
package leon
package facade

import
  akka.{
    NotUsed,
  },
  akka.actor.{
    ActorSystem,
  },
  akka.stream.{
    ActorMaterializer,
  },
  akka.stream.scaladsl.{
    Flow,
  },
  akka.http.scaladsl.{
    Http,
  },
  akka.http.scaladsl.model.{
    HttpRequest,
    HttpResponse,
  }

abstract class Leon(
  implicit
  val conf: config.Config,
  actorSystem: ActorSystem,
  materializer: ActorMaterializer,
)
  extends AnyRef
  with MirrorInitialization
{

  val httpExt = Http()
  val PackageSourceConfig = implicitly[config.PackageSource.Ext]
  val state = new State
  val mirrors: Set[Mirror] = loadMirrors

  trait WithHttp { val http = httpExt }
  trait WithLog { val log = actorSystem.log }
  trait WithState { val state = Leon.this.state }
  trait WithMaterializer { val materializer = Leon.this.materializer }
  trait WithMirrors { val mirrors = Leon.this.mirrors }

  final object PackageSource
    extends AnyRef
    with package_source.PackageSourceImpl
    with WithLog
  {
    val conf = PackageSourceConfig
    val ec = new package_source.PackageSourceImpl.FileOpsExecutionContext(actorSystem.dispatcher)
  }
  trait WithPackageSource { val packageSource = PackageSource }

  final object KeyMaker
    extends AnyRef
    with mirror.MirrorRequestToPackageSourceKeyImpl
  trait WithKeyMaker { val keyMaker = KeyMaker }

  final object DownloadManager
    extends AnyRef
    with DownloadManager[mirror.DownloadingMissingPackageHandler.Context]
    with WithHttp
    with WithMaterializer
  trait WithDownloadManager { val downloadManager = DownloadManager }

  final object MissingPackageHandler
    extends AnyRef
    with mirror.DownloadingMissingPackageHandler
    with WithLog
    with WithState
    with WithDownloadManager
    with WithMaterializer
  trait WithMissingPackageHandler { val missingPackageHandler = MissingPackageHandler }

  final object MirrorHttpRequestHandling
    extends AnyRef
    with mirror.MirrorHttpRequestHandling
    with WithLog
    with WithMissingPackageHandler
    with WithKeyMaker
    with WithPackageSource
    with WithMaterializer
  trait WithMirrorHttpRequestHandling { val mirrorHttpRequestHandling = MirrorHttpRequestHandling }

  final object HttpRequestToMirrorDispatcher
    extends AnyRef
    with http.HttpRequestToMirrorDispatcherImpl
    with WithMirrors
    with WithMirrorHttpRequestHandling

  val httpHandler: Flow[HttpRequest, HttpResponse, NotUsed] = HttpRequestToMirrorDispatcher()

}


