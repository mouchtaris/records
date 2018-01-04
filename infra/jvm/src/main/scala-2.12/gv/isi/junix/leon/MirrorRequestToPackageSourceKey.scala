package gv
package isi
package junix
package leon

import
  akka.http.scaladsl.model.{
    Uri,
  }

trait MirrorRequestToPackageSourceKey extends Any {

  def apply(mirror: Mirror, path: Uri.Path): PackageSource.Key.T

}

