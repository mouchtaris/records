package gv
package isi
package junix
package leon

import
  fun.{
    Named,
  },
  akkadecos._

object Mirror {

  object Name extends Named[String]

}

trait Mirror extends Any {

  def name: Mirror.Name.T
  def baseUris: Set[Uri]

}


