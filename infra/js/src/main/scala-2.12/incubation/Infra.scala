package incubation

import
  scalajs.js.annotation._,
  gv.{
    list,
    record,
  },
  list._,
  record._,
  infra.blue.model

@JSExportTopLevel("infra")
@JSExportAll
object Infra {

  @JSExportTopLevel("infra.Credentials")
  case class Credentials(id: String, key: String)

  @JSExportTopLevel("infra.SignUp")
  case class SignUp(creds: Credentials)

  @JSExportTopLevel("infra.LogIn")
  case class LogIn(creds: Credentials)

}
