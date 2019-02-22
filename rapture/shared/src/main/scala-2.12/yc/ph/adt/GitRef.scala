package yc
package ph
package adt


sealed trait GitRef extends Any

object GitRef {

  val ref: GitRef.type = this

  def random: commit = {
    val rand = new java.util.Random()
    val bs = Array[Byte](0, 0, 0, 0)
    rand.nextBytes(bs)
    commit(bs(0), bs(1), bs(2), bs(3))
  }

  final case class head(name: String) extends AnyRef with GitRef {
    override val toString: String = s"refs/head/$name"
  }
  final case class commit(b0: Byte, b1: Byte, b2: Byte, b3: Byte) extends AnyRef with GitRef with IndexedSeq[Byte] {

    override val length: Int = 4

    override def apply(idx: Int): Byte = idx match {
      case 0 ⇒ b0
      case 1 ⇒ b1
      case 2 ⇒ b2
      case 3 ⇒ b3
    }

    override val toString: String = {
      import commit.{ fbyte ⇒ f }
      val sb = StringBuilder.newBuilder
      (sb ++= f(b0) ++= f(b1) ++= f(b2) ++= f(b3)).result()
    }

  }

  object commit {

    val fbyte: Byte ⇒ String = "%02x".format(_)

    def apply(value: Long): commit = {
      val b0: Byte = ((value >> (8 * 0)) & 0xff).toByte
      val b1: Byte = ((value >> (8 * 1)) & 0xff).toByte
      val b2: Byte = ((value >> (8 * 2)) & 0xff).toByte
      val b3: Byte = ((value >> (8 * 3)) & 0xff).toByte
      apply(b0, b1, b2, b3)
    }

    def apply(value: String): commit = {
      apply(value.toLong)
    }

  }

}
