package gv
package isi
package junix
package leon
package config

import
  scala.collection.JavaConverters._


trait ConfigExt extends Any {

  def root: tsConfig

  final def get(name: String): tsConfig = root getConfig name

  /**
    * No validity check; call only if there are sub-objects.
    */
  final def subconfNames: Set[String] =
    root.root.keySet.asScala.toSet

}