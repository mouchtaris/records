package store

trait Crumb extends Any
  with StorageIdentifiable
{
  this: Any
    with StreamContext
    with FileContext
  ⇒

  def tags: Source[Tag]
}


