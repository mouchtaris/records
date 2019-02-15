package hatchjs
import org.scalajs.dom
import dom.document
import t.io.Println

object Main {

  def appendElem(elem: String)(targetNode: dom.Node)(text: String): Unit = {
    val parNode = document.createElement(elem)
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }

  final case class DomPrintln(root: dom.Node) extends Println {
    private[this] val append = appendElem("pre")(root) _
    override def apply(): Unit = ()
    override def apply(str: String): Unit = append(str)
    override def apply(obj: Any): Unit = this(obj.toString)
  }

  def main(args: Array[String]): Unit = {
    implicit val println: DomPrintln = DomPrintln(dom.document.body)
  }

}
