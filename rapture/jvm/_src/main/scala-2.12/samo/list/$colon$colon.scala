package samo.list

import xtrm.predef.Certainly

object :: {
  def unapply[H: HeadOf[L]#λ, T: TailOf[L]#λ, L](list: L): Certainly[(H, T)] =
    (list.head, list.tail)
}

