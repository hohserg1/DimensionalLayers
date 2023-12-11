package hohserg.dimensional.layers.gui

trait GuiElementControl[A] extends GuiElement {
  def originalValue: A

  def currentValue: A

  var changeHandler: () => Unit = () => {
    println("empty changeHandler")
  }

  private def checkStateAndMark(): Unit =
    if (originalValue != currentValue)
      changeHandler()

  override def mouseInput: Option[(Int, Int) => Unit] = super.mouseInput.map(enrich2)

  override def mouseClick: Option[(Int, Int, Int) => Unit] = super.mouseClick.map(enrich3)

  override def mouseRelease: Option[(Int, Int, Int) => Unit] = super.mouseRelease.map(enrich3)

  override def keyTyped: Option[(Char, Int) => Unit] = super.keyTyped.map(enrich2)

  private def enrich2[P1, P2](f: (P1, P2) => Unit): (P1, P2) => Unit =
    (p1, p2) => {
      f(p1, p2)
      checkStateAndMark()
    }

  private def enrich3[P1, P2, P3](f: (P1, P2, P3) => Unit): (P1, P2, P3) => Unit =
    (p1, p2, p3) => {
      f(p1, p2, p3)
      checkStateAndMark()
    }
}
