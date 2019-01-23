package store

trait StorageIdentifiable extends Any {
  final type StorageId = Long
  def id: StorageId
}

