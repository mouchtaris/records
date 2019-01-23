package lr1
package algebra

class Start {
  def apply[G](grammar: G)(implicit ev: dsl.To[G, adt.Grammar]) = ???
}
