package yc
package ph
package detail
package service
package store

import scala.collection.mutable

package object memory {

  type Repo = mutable.Map[adt.ComponentInstance, adt.ComponentReport]

  type Repos = (
    Unit,
    Repo,
  )

  def empty: Repo = mutable.Map.empty

  def repos: Repos = ((), empty)

}
