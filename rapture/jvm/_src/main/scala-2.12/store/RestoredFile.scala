package store

trait RestoredFile extends Any
  with StorageIdentifiable
{
  this: Any
    with FileContext
  ⇒

  def file: File
}


