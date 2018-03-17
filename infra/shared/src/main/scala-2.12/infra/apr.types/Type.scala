package infra
package apr
package types

trait Type
  extends AnyRef
  with The
{
  type Base

  sealed trait Tag extends Any

  final type Tagged = Base with Tag
  final type t = Tagged

  def apply(b: Base): Tagged = b.asInstanceOf[Tagged]

  def unapply(t: Tagged): Option[Base] = Some(t)
}
