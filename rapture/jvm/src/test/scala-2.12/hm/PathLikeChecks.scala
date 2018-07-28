package hm

import
  org.scalacheck._,
  Prop._

object PathLikeChecks
  extends Properties("PathLike")
{
  import Arbitrary.arbitrary

  final class Impl(val elements: PathLike.Elements)
    extends AnyRef
    with PathLike[Impl]

  final object Impl {
    implicit def apply(elements: PathLike.Elements): Impl =
      new Impl(elements)
    implicit def arg: Arbitrary[Impl] =
      Arbitrary(arbitrary[PathLike.Elements] map Impl.apply)
  }

  property("path") = forAll { path: Impl ⇒
    path.path == path.elements.mkString("/")
  }

  property("from") = forAll { (p: Impl, q: Impl) ⇒
    p.from(q).elements.size == p.elements.size + q.elements.size
  }

  property("/ other") = forAll { (p: Impl, q: Impl) ⇒
    (p / q).elements.size == p.elements.size + q.elements.size
  }

  property("/ string") = forAll { (p: Impl, s: String) ⇒
    (p / s).elements.size == p.elements.size + 1
  }

}
