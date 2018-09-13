package gv
package isi
package junix
package leon

import
  scala.collection.JavaConverters._,
  akkadecos._

package object config
  extends AnyRef
  with ConfigImplications
{

  type tsConfig = com.typesafe.config.Config

  type Config = Config.Ext

  object Config {

    def apply(root: tsConfig) = new Config(root)

    final class Ext(val root: tsConfig)
      extends AnyVal
      with ConfigExt
    {

      def MIRRORS = "mirrors"
      def mirrors: Mirrors.Ext = get { MIRRORS }

      def PACKAGE_SOURCE = "package_source"
      def package_source: PackageSource.Ext = get { PACKAGE_SOURCE }

      def SERVER = "server"
      def server: Server.Ext = get { SERVER }

    }

  }

}
