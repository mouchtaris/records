package t.fn
package pfs

sealed trait Make[S] extends Any with Def[Make[S]]

object Make {

  final implicit class Inst[S](val unit: Unit) extends AnyVal with Make[S]

}

