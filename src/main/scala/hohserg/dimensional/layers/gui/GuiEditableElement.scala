package hohserg.dimensional.layers.gui

trait GuiEditableElement[A] extends GuiElement {

  def updateVisual(v: A): Unit

}
