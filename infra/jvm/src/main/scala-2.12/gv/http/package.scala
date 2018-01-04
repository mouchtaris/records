package gv

import
  akkadecos._

package object http {

  type Handler =
    Flow[HttpRequest, HttpResponse, NotUsed]

  object Handler {

    implicit def toIdentityFlow(me: this.type): Flow[HttpRequest, HttpRequest, NotUsed] =
      Flow fromFunction identity

    def apply(f: HttpRequest ⇒ HttpResponse): Handler =
      Flow fromFunction f

  }

}
