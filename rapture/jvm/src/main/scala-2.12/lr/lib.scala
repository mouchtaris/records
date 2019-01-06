package lr

object lib {

  trait Lens[O, V] extends (O ⇒ V)

  trait Lensor[O] extends Any {

    type Lens[V] = lib.Lens[O, V]

  }

  final implicit class Debuggation[T](val self: T) extends AnyVal {
    def tap(f: T ⇒ Unit): T = { f(self); self }
    def tf[A](f: A ⇒ Unit)(get: T ⇒ A): T = { f(get(self)); self }
    def tpl[A](get: T ⇒ A): T = tf(println)(get)

    private[this] def ptap(f: ⇒ Unit): T = tap(_ ⇒ f)
    private[this] def prln(s: String, sbj: String = self.toString): Unit = println(s"[db5:$s] $sbj")
    private[this] def pln(s: String): T = ptap { prln(s) }

    def dbI0: T = pln("I 0")
    def dbI1: T = pln("I 1")
    def dbLookedAt: T = pln("looked at")
    def dbLookingAt: T = pln("looking at")
    def dbSelectedLookingAt: T = pln("selected looking at")
    def dbAdvancedStates: T = pln("advanced states")
    def dbClosed: T = pln("closed")
    def dbRenamed: T = pln("renamed")
    def dbReused: T = pln("reused")
    def dbNextId: T = pln("next id")

  }

}
