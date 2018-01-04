package gv
package isi
package junix
package leon
package mirror

trait MirrorManagerFromMirrorsConfig
  extends Any
  with MirrorManager
{

  def conf: config.Mirrors.Ext

  final def apply(): Set[leon.Mirror] = {
    val _conf = conf
    _conf.subconfNames map { name ⇒
      mirror.Mirror(
        name = leon.Mirror.Name(name),
        baseUris = _conf(name).bases.toSet
      )
    }
  }

}

