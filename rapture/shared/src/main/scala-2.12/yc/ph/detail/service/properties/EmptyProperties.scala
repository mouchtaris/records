package yc
package ph
package detail
package service
package properties

trait EmptyProperties
  extends Any
  with adt.ServiceProperties
{

  override def techOwner: Option[String] = None

  override def productOwner: Option[String] = None

}