package t.fn.pf
import t.fn.{pf ⇒ pkg}

trait Imports {

  type Pf[F, In] = pkg.Pf[F, In]
  val Pf: pkg.Pf.type = pkg.Pf

  type Def[F] = pkg.Def[F]
  val Def: pkg.Def.type = pkg.Def

  type Definition[-F, -A, B] = pkg.Definition[F, A, B]
  val Definition: pkg.Definition.type = pkg.Definition

  type Compose[F, G] = t.fn.pf.compose.Compose[F, G]
  val Compose: t.fn.pf.compose.Compose.type = compose.Compose

}
