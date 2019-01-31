package t.fn
package list.reduce

trait ReduceOps[S] extends Any {

  def self: S

  final def reduce[F,Zero](zero: Zero)(f: F)(
    implicit
    ev: Pf[Reduce[Zero, F], (Zero, S)],
  ): ev.Out =
    ev((zero, self))

}
