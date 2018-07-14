package detail.store

import _root_.store.FileContext

object FileContexts {
  trait AkkaAndScala extends Any
    with FileContext
    with AkkaStreamContext
    with ScalaFutureContext

  def apply(): AkkaAndScala = new AkkaAndScala { }
}


