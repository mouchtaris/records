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

  def run = ()

}

object Main {

  def main(args: Array[String]): Unit = {
    (new Main).run
  }


}
