package gv
package isi
package junix
package leon
package mirror

import
  java.nio.{
    CharBuffer,
    ByteBuffer,
    BufferOverflowException,
  },
  scala.util.{
    Try,
    Success,
    Failure,
    control,
  },
  akka.http.scaladsl.model.{
    Uri,
  }

trait MirrorRequestToPackageSourceKeyImpl
  extends AnyRef
  with MirrorRequestToPackageSourceKey
{

  private[this] object state {

    val HEXCHARS = "0123456789abcdef".toVector
    def DIGESTED_BYTES_LENGTH = 256 / 8
    def DIGESTOR = s"SHA-${DIGESTED_BYTES_LENGTH * 8}"
    import ByteBuffer.{
      allocate ⇒ allocateBB
    }

    var byteBuffer = allocateBB(4)
    val encoder = java.nio.charset.StandardCharsets.UTF_8.newEncoder()
    val digestor = java.security.MessageDigest.getInstance(DIGESTOR)
    val digestedString = java.nio.CharBuffer.allocate(DIGESTED_BYTES_LENGTH * 2)

    val catch22 = control.Exception.nonFatalCatch[Unit]

    object Victim {

      def apply(cb: CharBuffer): Victim =
        new Victim(cb)

      def apply(str: String): Victim =
        apply(CharBuffer wrap str)

    }

    final class Victim(victim: CharBuffer) {

      val requiredCapacity = encoder.maxBytesPerChar.toInt * victim.length

      def grow(): Unit =
        if (byteBuffer.capacity < requiredCapacity)
          byteBuffer = allocateBB(requiredCapacity)

      def encode(): Try[Unit] = {
        grow()
        byteBuffer.clear()
        encoder.reset()

        catch22 withTry {
          encoder.encode(victim, byteBuffer, true) match {
            case result if result.isError ⇒
              result.throwException()
            case _ ⇒
              if (victim.hasRemaining)
                encode()
              else
                Success(())
          }
        }
      }

      def digest(): Try[Array[Byte]] = encode() map { _ ⇒
        digestor.reset()
        byteBuffer.flip()
        digestor.update(byteBuffer)
        digestor.digest()
      }

      def hex(): Try[Unit] = digest() map { digestedBytes ⇒
        digestedString.clear()
        digestedBytes.zipWithIndex foreach {
          case (byte, index) ⇒
            val msc = HEXCHARS((byte & 0x000000F0) >> 4)
            val lsc = HEXCHARS((byte & 0x0000000F) >> 0)
            digestedString.put(msc)
            digestedString.put(lsc)
        }
      }

      def hexdigest: Try[String] = hex() map { _ ⇒
        digestedString.flip()
        digestedString.toString
      }

    }

  }

  final def apply(mirror: leon.Mirror, path: Uri.Path): PackageSource.Key.T =
    PackageSource.Key {
      state.Victim(mirror.toString ++ path.toString).hexdigest match {
        case Success(key) ⇒
          key
        case Failure(ex) ⇒
          throw ex
      }
    }
}
