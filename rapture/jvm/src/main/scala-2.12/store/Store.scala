package store

trait Store extends Any {
  this: Any
    with FileContext
    with StreamContext
  =>

  def store: Flow[File, Crumb]
  def restore: Flow[StorageIdentifiable#StorageId, RestoredFile]
}


