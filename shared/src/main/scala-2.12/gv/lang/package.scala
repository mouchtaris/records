package gv

package object lang {

  type Implicitly[a] = a

  def use[T](t : T): Unit = ()

}
