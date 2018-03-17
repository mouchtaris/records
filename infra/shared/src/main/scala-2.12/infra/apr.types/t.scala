package infra
package apr
package types

object t {

  trait String extends TypeB[scala.Predef.String]
  trait Int extends TypeB[scala.Int]
  trait Boolean extends TypeB[scala.Boolean]

}
