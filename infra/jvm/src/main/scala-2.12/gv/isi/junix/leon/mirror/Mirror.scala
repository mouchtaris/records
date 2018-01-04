package gv
package isi
package junix
package leon
package mirror

import
  akkadecos.{
    Uri,
  }

final case class Mirror(
  name: leon.Mirror.Name.T,
  baseUris: Set[Uri]
)
  extends AnyRef
  with leon.Mirror
