package gv.string

trait ToStringDeco {

  final implicit def toStringDeco[s: ToString](_s: s): ToStringOps[s] =
    new ToStringOps[s] with ImplicitToString[s] {
      def self: s = _s
      def implicitToString: ToString[s] = implicitly
    }

}
