package yc
package ph
package detail
package service
package store

import scala.collection.mutable

package object memory {

  type Repo = mutable.Map[adt.ServiceInstance, adt.ServiceReport]

  type Repos = (
    Unit,
    Repo,
  )

  def empty: Repo = mutable.Map.empty

  def repos: Repos = ((), empty)

}
