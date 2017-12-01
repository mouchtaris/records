package gv
package building
package defaultconstructible

trait Defaults {

  implicit def stringBuilderDefaultConstructible: DefaultConstructible[StringBuilder] =
    () ⇒ new StringBuilder

}
