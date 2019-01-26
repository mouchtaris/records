package fn.pf
import fn.{ pf ⇒ pkg }

trait Imports {

  type Pf[F, In] = pkg.Pf[F, In]
  val Pf: pkg.Pf.type = pkg.Pf

  type Def[F] = pkg.Def[F]
  val Def: pkg.Def.type = pkg.Def

  type Compose[F, G] = pkg.Compose[F, G]
  val Compose: pkg.Compose.type = pkg.Compose

}
