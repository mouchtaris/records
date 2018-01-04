package gv
package isi
package junix

import
  akkadecos._,
  fun._,
  lang._,
  string._,
  tag._

package object leon {

  val httpRequestHandler: Flow[HttpRequest, HttpResponse, NotUsed] = ???

  class State(
    implicit
    actorSystem: ActorSystem,
  ) {

    def report(): Unit = privates.actor ! messages.Report
    def downloading(id: String): Unit = privates.actor ! messages.Downloading(id)
    def sipping(id: String): Unit = privates.actor ! messages.Sipping(id)
    def done(id: String): Unit = privates.actor ! messages.Done(id)

    object messages {
      case object Report
      case class Downloading(id: String)
      case class Sipping(id: String)
      case class Done(id: String)
    }

    final object privates {

      class Actor extends akka.actor.Actor {
        val downloading = scala.collection.mutable.Set.empty[String]
        val sipping = scala.collection.mutable.Set.empty[String]

        def report(): Unit = {
          println(s"=== Downloading: ${downloading.size} ===")
          println(downloading.map("+ " ++ _).mkString("\n"))
          println(s"    Sipping:     ${sipping    .size} ===")
          println(sipping    .map("* " ++ _).mkString("\n"))
        }

        def receive: Receive = {
          case messages.Report ⇒
            report()
          case messages.Downloading(id) ⇒
            downloading += id
            report()
          case messages.Sipping(id) ⇒
            sipping += id
            report()
          case messages.Done(id) ⇒
            downloading -= id
            sipping -= id
            report()
          case _ ⇒
            ()
        }
      }

      val actor: ActorRef = actorSystem.actorOf(Props(new Actor))
    }

  }

  class Leon(
    implicit
    conf: config.Config,
    actorSystem: ActorSystem,
    materializer: ActorMaterializer,
  ) extends facade.Leon

}