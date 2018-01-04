package gv
package isi
package junix
package leon
package facade

object MirrorInitialization {

  final class MirrorManager(val self: config.Mirrors.Ext.Self)
    extends AnyVal
    with mirror.MirrorManagerFromMirrorsConfig
  {
    def conf: config.Mirrors.Ext = self
  }

  object MirrorManager {
    def apply()(implicit conf: config.Mirrors.Ext): MirrorManager =
      new MirrorManager(conf.root)
  }

}

trait MirrorInitialization extends Any {
  import MirrorInitialization._

  protected[this] implicit def conf: config.Config

  def loadMirrors: Set[Mirror] =
    MirrorManager().apply()
}


