package gv

package object lang {

  type Implicitly[a] = a

  def use[T](t : T): Unit = ()

  final implicit class TapDecoration[T](val self: T) extends AnyVal {
    def tap(f: T ⇒ Unit): T = {
      f(self)
      self
    }
  }

  final implicit class LockDecoration[T](val self: T) extends AnyVal {
    def lock[R](block: T ⇒ R): R =
      block(self)
  }

  final implicit class ThrowableDecoration(val self: Throwable)
    extends AnyVal
    with ThrowableExt.Decoration

  /**
    * Indefinitely repeat, forever, the elements found in collection `c`,
    * in their right order.
    *
    * @param c a collection from which to repeat elements
    * @tparam T the type of elemtns in the collection
    * @return an infinite stream pf the elements in `c`
    */
  def streamOfInfinity[T](c: Traversable[T]): Stream[T] =
    c.toStream ++ streamOfInfinity(c)

  object Now extends scala.concurrent.ExecutionContext {
    override def execute(runnable: Runnable): Unit = runnable.run()
    override def reportFailure(cause: Throwable): Unit = throw cause
  }

}
