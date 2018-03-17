package infra
package nineties

import
  scala.util.{
    Success,
    Failure,
  },
  scala.concurrent.{
    Await,
    Future,
    ExecutionContext,
    duration,
  },
  scala.util.control.Exception,
  com.typesafe.config.{
    ConfigFactory,
    Config,
  },
  details.slick.{
    Tables
  },
  gv.{
    control,
  },
  gv.util.awaitable.{
    AwaitableOps,
    DurationDye,
  }

class Main {

  def run = {
    implicit val initec = new init.InitEc(gv.util.now)
    val inits = init.lz
    println(inits.all)
  }

}

object Main {

  def main(args: Array[String]): Unit = {
    (new Main).run
  }


}
