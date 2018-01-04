package gv
package isi
package junix
package leon

import
  scala.concurrent.{
    Future,
  },
  akka.{
    NotUsed,
  },
  akka.stream.scaladsl.{
    Flow,
    Sink,
  },
  akka.http.scaladsl.model.{
    Uri,
  },
  akka.util.{
    ByteString,
  }

trait MissingPackageHandler
  extends AnyRef
{

  type Context
  final type ByteSink = Sink[ByteString, Future[Any]]
  final type Input = (leon.Mirror, Uri.Path, ByteSink)

  val flow: Flow[Input, Context, NotUsed]

}
