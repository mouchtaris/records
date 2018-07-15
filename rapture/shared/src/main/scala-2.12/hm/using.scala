package hm

object using {

  def apply[T, R](obj: ⇒ T)(f: T ⇒ R): R = f(obj)

}
