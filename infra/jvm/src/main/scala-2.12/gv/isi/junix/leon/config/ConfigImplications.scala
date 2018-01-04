package gv
package isi
package junix
package leon
package config

trait ConfigImplications extends Any {

  final implicit def mirrorsFromConfig(implicit conf: Config): Mirrors.Ext =
    conf.mirrors

  final implicit def packageSourceFromConfig(implicit conf: Config): PackageSource.Ext =
    conf.package_source

}
