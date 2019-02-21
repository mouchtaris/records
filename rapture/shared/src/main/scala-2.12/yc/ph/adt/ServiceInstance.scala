package yc
package ph
package adt

final case class ServiceInstance(
  service: Service,
  at: java.time.Instant,
  commit: CommitId,
)
