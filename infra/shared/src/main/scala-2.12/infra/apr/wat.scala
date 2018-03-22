package infra
package apr

object wat {

  trait apply[pf, -x] extends Any { type result }
  object apply extends apply_impl.companion

  trait resultU[u, r <: u] extends Any { final type result = r }
  trait result[r] extends Any with resultU[Any, r]

  trait ==>[a, r] extends Any
  object ==> extends `==>_impl`.companion

  import apr.list.{ List, ::, Nil }

  trait list extends Any
  trait head extends Any
  object head {
    implicit def defined[h]: (head apply (h :: List)) ==> h =
      ==>.fromApplicationAndResult {
        (_ : h :: List).head
      }
  }
  trait tail extends Any
  object tail {
    implicit def defined[t <: List]: (tail apply (_ :: t)) with result[t] = apply()
  }


  def omg = {
    implicitly[ head apply (Int :: Nil) ]
  }

}

object apply_impl {
  implicit class Impl[pf, x, r](val self: Unit)
    extends AnyVal
      with wat.apply[pf, x]
      with wat.result[r]
  trait companion {
    implicit def apply[pf, x, r](): Impl[pf, x, r] = ()
  }
}

object `==>_impl` {
  import wat._

  trait companion {
    implicit def fromApplicationAndResult[pf, x, r](
      f: x => r
    ): (pf apply x) ==> r = ???
  }
}
