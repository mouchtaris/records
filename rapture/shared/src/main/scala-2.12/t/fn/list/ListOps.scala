package t.fn.list

trait ListOps[S <: List] extends Any {

  def self: S

  final def ::[h](h: h)(implicit dummyImplicit: DummyImplicit): h :: S =
    Cons(h, self)

}
