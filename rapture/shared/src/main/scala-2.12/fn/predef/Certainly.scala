package fn.predef

/**
  * Used for certain pattern matches.
  *
  * <p>
  * <pre>
  *     object Unmatch {
  *       def unapply[A](a: A): Certainly[A] = new Certainly(a)
  *     }
  *     3 match {
  *       case Unmatch(three) =>
  *         assert(three == 3)
  *     }
  * </pre>
  * </p>
  * @param get
  * @tparam A
  */
final class Certainly[+A](val get: A) extends AnyVal {
  def isEmpty: Boolean = false
}

