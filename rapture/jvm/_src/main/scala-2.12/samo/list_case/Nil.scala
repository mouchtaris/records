package samo
package list_case

case object Nil extends AnyRef with Cons {

  implicit val nilToString: to_string.ToString[this.type] =
    _ ⇒ "Nil"

}
