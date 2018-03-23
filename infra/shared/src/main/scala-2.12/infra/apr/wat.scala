package infra
package apr

object wat {

  import The._
  import apr.list.{ List, ::, Nil }
  import tdb._

  final class :=[pf, sig](val self: sig) extends AnyVal {
    final def apply[x, r](x: x)(implicit ev: sig <:< (x => r)): r = self(x)
  }

  trait head
  implicit def `list::head`[h]
  : head := ( (h :: List) => h ) =
    new :=(_.head)

  final implicit class HeadDeco[s](val self: s) extends AnyVal {
    def head[h](implicit ev: head := (s => h)): h = ev(self)
  }


  def omg = {
    ("Helo worl" :: true :: "bobo" :: "omg again" :: 12 :: Nil).head
  }

}
