package mse

trait domain {
  import xtrm.tag.SpecificTaggingContext
  import xtrm.list.{ ::, Nil }
  import xtrm.model.Record

  sealed trait New[V] extends Any with SpecificTaggingContext[V]

  final object cred {

    sealed trait Type
    object types {
      final case object Password extends Type
    }

    case object Data extends New[Array[Byte]]
    type Data = Data.Tagged
  }

  final object Email extends New[String]
  final type Email = Email.Tagged

  final type Account = Email :: cred.Type :: cred.Data :: Nil
  final val Account = Record[Account]

  final object Bio extends New[String]
  final type Bio = Bio.Tagged

}
