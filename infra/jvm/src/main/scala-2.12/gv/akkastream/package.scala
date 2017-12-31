package gv

import
  akka.stream.{
    Attributes,
  },
  akka.stream.scaladsl.{
    Flow,
  }

package object akkastream {

  final implicit class FlowDecoration[in, out, mat](val self: Flow[in, out, mat]) extends AnyVal {

    def withName(name: String) =
      self withAttributes Attributes(Attributes.Name(name))

  }

}
