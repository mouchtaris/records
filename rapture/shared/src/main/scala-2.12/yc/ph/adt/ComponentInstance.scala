package yc
package ph
package adt

final case class ComponentInstance(
  component: Component,
  at: java.time.Instant,
  commit: GitRef,
)
