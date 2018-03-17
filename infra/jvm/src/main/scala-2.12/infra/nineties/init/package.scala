package infra
package nineties

import
  scala.concurrent.{
    ExecutionContext,
  }

package object init {

  final class InitEc(val self: ExecutionContext)
    extends AnyVal
    with coloured.Coloured[ExecutionContext]

  final def lz(implicit ec: InitEc): InitPlace = new InitPlace

}
