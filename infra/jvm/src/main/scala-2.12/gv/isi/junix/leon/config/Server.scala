package gv
package isi
package junix
package leon
package config

object Server {

  val HOST: String = "host"
  val PORT: String = "port"

  final implicit class Ext(val value: tsConfig)
    extends AnyVal
    with ConfigExt
  {
    def host: String = value getString HOST
    def port: Int = value getInt PORT
  }

}
