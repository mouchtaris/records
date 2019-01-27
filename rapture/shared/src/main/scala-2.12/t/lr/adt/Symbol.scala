package t.lr
package adt

trait Symbol[S] extends Any with t.fns.TypeClass[S] {

  def name: R[String]

}
