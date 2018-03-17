package infra
package apr
package list

sealed trait Nil
  extends AnyRef
    with List
    with The

case object Nil extends Nil
