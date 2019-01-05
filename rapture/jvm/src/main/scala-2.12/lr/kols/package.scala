package lr

import scala.collection.IndexedSeqOptimized

package object kols {

  trait ExpandingIndexedSeq[T] extends Any with IndexedSeqOptimized[T, ExpandingIndexedSeq[T]]

}
