package yc
package ph
package service

trait Browsing extends Any {

  /**
    * @return all available components.
    */
  def components: Stream[adt.Component]

  /**
    * Return the latest instance of a component.
    *
    * @param component a component to look up
    * @return the latest component instance or `None` if such component
    *         does not exist
    */
  def latest(component: adt.Component): Option[adt.ComponentInstance]

}
