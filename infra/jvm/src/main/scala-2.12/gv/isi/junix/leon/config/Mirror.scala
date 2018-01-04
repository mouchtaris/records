package gv
package isi
package junix
package leon
package config

import
  scala.collection.JavaConverters._,
  akkadecos.{
    Uri,
  }

object Mirror {

  final implicit class Ext(val root: tsConfig)
    extends AnyVal
    with ConfigExt
  {

    def BASES = "bases"

    def bases: Traversable[Uri] = (root getStringList BASES).asScala map (Uri(_))

  }

}


