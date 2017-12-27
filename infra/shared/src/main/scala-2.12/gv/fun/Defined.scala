package gv
package fun

trait Defined[tag, in, +out] extends Any with PartialFunction[tag, in, out] {

  def apply(): scala.PartialFunction[in, out]

  final def apply(in: in): out =
    this()(in)

}
