package lr
package adt

trait Symbol[S] extends Any with fns.TypeClass[S] {

  def name: R[String]

}
