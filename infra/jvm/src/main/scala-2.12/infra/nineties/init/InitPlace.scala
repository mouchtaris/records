package infra
package nineties
package init

import
  scala.concurrent.{
    ExecutionContext,
  }

abstract class InitPlace(
  implicit
  protected[this] val executionContext: ExecutionContext
)
  extends AnyRef
    with InitConfig
    with InitActorSystem
    with InitStreamSystem
    with InitDatabase
{
  def config: InitConfig.Out
  def actorSystem: InitActorSystem.Out
  def streamSystem: InitStreamSystem.Out
  def database: InitDatabase.Out
}
