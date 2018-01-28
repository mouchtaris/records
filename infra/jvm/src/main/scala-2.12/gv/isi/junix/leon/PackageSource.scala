package gv
package isi
package junix
package leon

import
  scala.concurrent.{
    Future,
  },
  akka.stream.{
    IOResult,
  },
  akka.stream.scaladsl.{
    Source,
    Sink,
  },
  akka.util.{
    ByteString,
  },
  fun.{
    Named,
  }


object PackageSource {

  object Key extends Named[String]

  sealed trait Result extends Any
  object Results {
    final case class Found(key: Key.T, length: Long, source: Source[ByteString, Any]) extends Result
    final case class Later(key: Key.T) extends Result
    final case class Failed(key: Key.T) extends Result
    final case class Missing(key: Key.T, sink: Sink[ByteString, Future[IOResult]]) extends Result
  }
}

trait PackageSource {

  import PackageSource.{
    Key,
    Result,
  }

  def apply(key: Key.T): Result

}

