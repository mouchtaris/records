package samo

object Examples {

  trait generic_list_provider extends Any {
    import list.{
      Head,
      Tail
    }
    sealed trait `12 :: hello`
    sealed trait `hello`
    object hello extends hello

    sealed trait List extends `12 :: hello`
    def List(): List = new List { }
    implicit def firstHead: `12 :: hello` Head Int = _ ⇒ 12
    implicit def firstTail: `12 :: hello` Tail hello = hello
  }
  object generic_list_provider extends generic_list_provider

  object pfs {
    import pf.PF
    sealed trait Plus1
    implicit def intPlus1: PF[Plus1, Int, Int] = _ + 1
    implicit def stringPlus1: PF[Plus1, String, String] = _.toString + "1"
  }

  def use[T](obj: T): Unit = obj

  def test(): Unit = {
    test_generic_list()
    test_list_map()
  }

  def test_generic_list(): Unit = {
    import list._
    import generic_list_provider.List
    val li = List()
    use { li.head: Int }
    use { li.tail: List }
  }

  def test_list_map(): Unit = {
    import list._
    import list_map._
    import generic_list_provider.List
    val li = List()
    val mapped = Map[pfs.Plus1](li)
    assert(mapped.head == 13)
  }

}
