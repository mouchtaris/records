package samo

package object pf {

  type Defined[F] = {
    type At[A] = {
      type Result[B] = PF[F, A, B]
    }
  }

}
