package hohserg.dimensional.layers.gui

trait GuiElement {
  def draw: Option[(Int, Int, Float) => Unit] = None

  def mouseInput: Option[(Int, Int) => Unit] = None

  def mouseClick: Option[(Int, Int, Int) => Unit] = None

  def mouseClickMove: Option[(Int, Int, Int) => Unit] = None

  def mouseRelease: Option[(Int, Int, Int) => Unit] = None

  def keyTyped: Option[(Char, Int) => Unit] = None

}
