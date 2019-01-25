package lr
package adt

trait Symbol[S] extends Any with fn.TypeClass[S] {

  def name: R[String]

}
