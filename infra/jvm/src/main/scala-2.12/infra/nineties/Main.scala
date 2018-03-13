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

  implicit val waitingDuration: DurationDye = duration.Duration("5 seconds")
  import ExecutionContext.Implicits.global

  def haveFunWith(components: init.Components): Future[Unit] = {
    import details.slick.Tables
    import Tables.profile.api._

    components.database
      .run { Tables.users.take(2).result }
      .map(println)
  }

  def run(): Unit = {
    println("Hello Mom")
    val inits = init.All()
    try {
      val allnall: Future[Unit] = inits.components.flatMap(haveFunWith)
      println {
        s" All n all .. all n all ...... ${allnall.awaitResult}"
      }
    }
    finally {
      println(s"Cleeeeeaning up... ")
      println(s"CLeanup done: ${inits.cleanUp().awaitResult}")
    }
  }


}

object Main {

  def main(args: Array[String]): Unit = {
    (new Main).run()
  }


}
