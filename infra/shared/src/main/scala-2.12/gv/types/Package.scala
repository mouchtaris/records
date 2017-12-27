package gv
package types

import
  list._

trait Package {

  trait TBoolean extends Any
  trait TTrue extends Any with TBoolean
  trait TFalse extends Any with TBoolean

  trait TypePartialFunction[tag, in, out] extends Any
  trait TypeDefined[tag, in, out] extends Any with TypePartialFunction[tag, in, out]
  object TypeDefined {
    type result[tag, in] = { type λ[out] = TypeDefined[tag, in, out] }
  }
  trait TypeNotDefined[tag, in] extends Any with TypePartialFunction[tag, in, Nothing]
  object TypeNotDefined {
    type at[in] = { type λ[tag] = TypeNotDefined[tag, in] }
  }

  trait TypeMap[tag, list <: List, out <: List] extends Any
  object TypeMap {
    implicit def nilMap[tag]: TypeMap[tag, Nil, Nil] = null
    implicit def listMap[tag, h, t <: List, out, tout <: List](implicit tf: TypeDefined[tag, h, out], tmap: TypeMap[tag, t, tout]): TypeMap[tag, h :: t, out :: tout] = null
  }

  trait TypeZip[tag, a <: List, b <: List, out <: List] extends Any
  object TypeZip {
    final type resultOf[tag, a <: List, b <: List] = {
      type λ[out <: List] = TypeZip[tag, a, b, out]
    }
    implicit def nilZipA[tag, b <: List]: TypeZip[tag, Nil, b, Nil] = null
    implicit def nilZipB[tag, a <: List]: TypeZip[tag, a, Nil, Nil] = null
    implicit def zipList[tag, ha, ta <: List, hb, tb <: List, tzOut <: List: resultOf[tag, ta, tb]#λ]: TypeZip[tag, ha :: ta, tb :: tb, (ha, hb) :: tzOut] = null
  }

  trait TypeSelect[tag, list <: List, out <: List] extends Any
  object TypeSelect {
    type resultOf[tag, list <: List] = { type λ[out <: List] = TypeSelect[tag, list, out] }
    implicit def nilSelect[tag]: TypeSelect[tag, Nil, Nil] = null
    implicit def definedSelect[
      tag,
      h,
      t <: List,
      out: TypeDefined.result[tag, h]#λ,
      tout <: List: TypeSelect.resultOf[tag, t]#λ
    ]: TypeSelect[tag, h :: t, out :: tout] = null
    implicit def notDefinedSelect[
      tag: TypeNotDefined.at[h]#λ,
      h,
      t <: List,
      tout <: List: TypeSelect.resultOf[tag, t]#λ
    ]: TypeSelect[tag, h :: t, tout] = null
  }

  trait TypeFind[tag, list <: List, out] extends Any
  object TypeFind {
    implicit def findEvidence[
      tag,
      list <: List,
      sel <: List: TypeSelect.resultOf[tag, list]#λ,
      h: Evidence.headOf[sel]#λ
    ]: TypeFind[tag, list, h] = null
  }

}
