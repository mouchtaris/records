package infra
package nineties
package init

import
  scala.concurrent.{
    ExecutionContext,
  }

trait Init[R]
  extends AnyRef
{
  final type Close = R ⇒ Future[Unit]
  final type Result = Future[R]

  protected[this] def initialize(): Result
  protected[this] def close: Close = _ ⇒ Future.successful(())

  protected[this] val onInitializeHooks: Seq[Option[R] ⇒ Unit] = Seq(
    {
      case Some(r) ⇒ println(s"[[[ ${Init.this.toString} ]]]: initialized $r")
      case None ⇒ println(s"[[[ ${Init.this.toString} ]]]: initializing . . .")
    }
  )
  protected[this] val onCleanUpHooks: Seq[String ⇒ Unit] = Seq(
    n ⇒ println(s"[[[ $n ]]]: cleaning up . . .")
  )

  private[this] def notifyInitialized(result: R): R = {
    onInitializeHooks foreach (_ (Some(result)))
    result
  }
  private[this] def notifyInitializing(): Unit =
    onInitializeHooks foreach (_ (None))
  private[this] def notifyCleaningUp(): Unit =
    onCleanUpHooks foreach (_(toString))

  final lazy val result: Result = {
    notifyInitializing()
    initialize().map(notifyInitialized)
  }
  final def cleanUp(): Future[Unit] = {
    notifyCleaningUp()
    result flatMap close
  }
  final implicit val executionContext: ExecutionContext = ExecutionContext.global

  final def map[U](f: R ⇒ U): Future[U] = result map f
  final def zip[U](other: Init[U]): Init[(R, U)] =
    new Init[(R, U)] {
      def initialize() = Init.this.result zip other.result
    }

}


