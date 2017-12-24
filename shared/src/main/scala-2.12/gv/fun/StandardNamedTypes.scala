package gv
package fun

trait StandardNamedTypes extends Any {

  trait ScopedNamed[T] extends AnyRef with Named[T]

  trait boolean extends AnyRef with ScopedNamed[Boolean]
  trait int extends AnyRef with ScopedNamed[Int]
  trait string extends AnyRef with ScopedNamed[String]

}

