package gv
package fun

trait NotDefined[tag, in] extends Any with PartialFunction[tag, in, Nothing]

object NotDefined {

  implicit def genericNotDefined[tag, in]: NotDefined[tag, in] = apply()

  def apply[tag, in](): NotDefined[tag, in] = null

}


