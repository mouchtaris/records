package gv
package isi
package junix
package leon
package config

object Mirrors {

  object Ext {
    type Self = tsConfig
  }

  final implicit class Ext(val root: tsConfig)
    extends AnyVal
    with ConfigExt
  {

    def apply(name: String): Mirror.Ext = root getConfig name

  }

}


