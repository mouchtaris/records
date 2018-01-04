package gv
package isi
package junix
package leon
package config

object PackageSource {

  final implicit class Ext(val root: tsConfig) extends AnyVal {

    def ROOT = "root"
    def storageRoot: String = root getString ROOT

  }

}
