package infra
package nineties
package init

import
  apr._,
  list._

sealed class InitPlace(
  implicit
  protected[this] val executionContext: InitEc
)
  extends AnyRef
    with InitConfigMixin
    with InitActorSystemMixin
    with InitStreamSystemMixin
    with InitDatabaseMixin
    with InitHttpServerMixin
    with coloured.Conversions
{
  final lazy val config: InitConfig.t = ???
  final lazy val actorSystem: InitActorSystem.t = ???
  final lazy val streamSystem: InitStreamSystem.t = ???
  final lazy val database: InitDatabase.t = ???
  final lazy val httpServer: InitHttpServer.t = ???

  final type All =
    InitConfig.t ::
    InitActorSystem.t ::
    InitStreamSystem.t ::
    InitDatabase.t ::
    InitHttpServer.t ::
    Nil
  final lazy val all: All = ???

  def wat = {
  }
}
