package lart
package setup

import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.UTF_8

import akka.actor.{Actor, ActorRef, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import hm.TypeInfo

object TcpListener
  extends AnyRef
  with TypeInfo[TcpListener]

class TcpListener(
  implicit
  akkaContext: AkkaContext,
  logs: LoggingContext,
) {

  private[this] val logger = logs.factory[TcpListener]

  logger.info("Good morning")

  private[this] def localAddress: InetSocketAddress =
    new InetSocketAddress("0.0.0.0", 26000)

  class Handler extends Actor {
    import Tcp._
    import context.system

    IO(Tcp) ! Bind(self, localAddress)

    final case object Stop

    override def receive: Receive = {
      case b @ Bound(address) ⇒
        logger.info("Bound to address: {}", address)
      case CommandFailed(bind: Bind) ⇒
        logger.error("Binding failed: {}", bind)
        self ! Stop
      case c @ Connected(remote, local) ⇒
        logger.info("Accepted {} on {}", remote, local)
        val connection = sender()
        connection ! Register(self)
      case Received(data) ⇒
        logger.info("Received: {}", data.decodeString(UTF_8))
        sender() ! Write(ByteString(
          "HTTP/1.1 200 OK\r\n" +
          "Content-type: text/plain\r\n" +
          "Content-length: 13\r\n" +
          "Access-Control-Allow-Origin: https://staging.musae.me:9081\r\n" +
          "Access-Control-Allow-Credentials: true\r\n" +
          "\r\n" +
          "OK RE MAN MOY"
        ))
      case _: ConnectionClosed ⇒
        logger.info("Closing connection")
        self ! Stop
      case Stop ⇒
        logger.info("Stoppping")
        context stop self
      case any ⇒
        logger.info("Here is a message: {}", any)
    }
  }

  def handler: ActorRef = akkaContext.actorSystem.actorOf(Props(new Handler))
}
