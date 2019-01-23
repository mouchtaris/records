package samo
package list_map

import
  list._,
  pf._

final class Map[L, F](val value: L) extends AnyVal

object Map {

  final class BindF[F](val unit: Unit) extends AnyVal {
    def apply[L](list: L): L Map F = new Map(list)
  }

  def apply[F] = new BindF[F](())

  implicit def head[
    L,
    F,
    H: List[L]#Head,
    R: Defined[F]#At[H]#Result,
  ]: L Map F Head R =
    mapped ⇒
      PF[F](mapped.value.head)

  implicit def tail[
    L,
    F,
    T: List[L]#Tail,
  ]: L Map F Tail (T Map F) =
    mapped ⇒
      Map[F](mapped.value.tail)
}
