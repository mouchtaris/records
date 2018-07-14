package store
package file_context


trait FileExtension extends Any {
  this: Any
    with StreamContext
    with TagExtension
  ⇒

  trait File extends Any {
    def tags: Source[Tag]
    def data: Source[Byte]
  }

}
