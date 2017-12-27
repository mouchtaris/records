package gv

package object list
  extends AnyRef
  with list.Package
  with ImplicitConversions
  with ToListDeco
  with ToTupleDeco
{

  final implicit class ListDeco[self <: List](val self: self)
    extends AnyVal
    with ListBuilding[self]

}
