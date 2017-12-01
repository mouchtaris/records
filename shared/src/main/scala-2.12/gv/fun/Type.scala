package gv
package fun

trait Type extends Any {

  type T

}

object Type {

  type of[t] = Type {
    type T = t
  }

}
