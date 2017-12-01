package gv
package string

trait WritableDeco extends Any {

  final implicit def writableDeco[w: Writable](w: w): WritableOps[w] =
    new WritableOps[w] with ImplicitWritable[w] {
      def self: w = w
      def implicitWritable: Writable[w] = implicitly
    }

}
