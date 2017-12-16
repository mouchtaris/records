package gv
package fun

trait StandardNamedTypes extends Any {

  trait ScopedNamed[T] extends AnyRef with Named[T]

  trait int extends AnyRef with ScopedNamed[Int]
  trait string extends AnyRef with ScopedNamed[String]

}

