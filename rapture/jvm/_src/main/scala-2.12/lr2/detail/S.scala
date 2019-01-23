package lr2
package detail

object S {

  sealed abstract class Symbol(nameValue: String) {
    implicit object Symbol extends adt.Symbol[this.type] {
      override val name: R[String] = _ ⇒ nameValue
    }
  }

  object id extends Symbol("id")
  object + extends Symbol("+")
  object * extends Symbol("*")
  object factor extends Symbol("factor")
  object term extends Symbol("term")
  object expr extends Symbol("expr")
  object goal extends Symbol("goal")

}
