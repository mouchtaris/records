package store

trait FileContext extends Any
  with file_context.TagExtension
  with file_context.FileExtension
{
  this: Any
    with StreamContext
    with FutureContext
  ⇒
}


