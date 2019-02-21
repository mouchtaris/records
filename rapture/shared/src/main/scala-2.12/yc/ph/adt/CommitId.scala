package yc
package ph
package adt

final class CommitId(val value: Vector[Byte]) extends AnyVal {

  override def toString: String = {
    val fbyte = "%02x".format(_: Byte)
    value.map(fbyte).mkString
  }

}

object CommitId {

  def apply(value: Vector[Byte]) = new CommitId(value)

  def random: CommitId = {
    val bytes = (0 to 3).map(_.toByte).toArray[Byte]
    new java.util.Random().nextBytes(bytes)
    apply(bytes.toVector)
  }

}
