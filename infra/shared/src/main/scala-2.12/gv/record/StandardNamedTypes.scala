package gv
package record

import
  fun._

trait StandardNamedTypes extends AnyRef {
  this: Record ⇒

  sealed trait ScopedNamed[T] extends AnyRef with Named[T] {
    final type Record = StandardNamedTypes.this.type
    final val Record: Record = StandardNamedTypes.this
  }

  trait boolean extends AnyRef with ScopedNamed[Boolean]
  trait int extends AnyRef with ScopedNamed[Int]
  trait string extends AnyRef with ScopedNamed[String]

}


