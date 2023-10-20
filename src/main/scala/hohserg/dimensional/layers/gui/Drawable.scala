package hohserg.dimensional.layers.gui

trait Drawable {
  def draw(minX: Int, minY: Int, maxX: Int, maxY: Int): Unit

  def tooltip: String

}
