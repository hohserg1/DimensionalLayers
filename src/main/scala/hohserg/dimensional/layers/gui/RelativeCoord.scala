package hohserg.dimensional.layers.gui

import scala.language.implicitConversions

trait RelativeCoord {
  def absoluteCoord(x: Int, y: Int, w: Int, h: Int): Int
}

object RelativeCoord {

  def alignLeft(offset: Int): RelativeCoord =
    (x: Int, _: Int, _: Int, _: Int) => x + offset

  def alignRight(offset: Int): RelativeCoord =
    (_: Int, _: Int, w: Int, _: Int) => w + offset

  def alignTop(offset: Int): RelativeCoord =
    (_: Int, y: Int, _: Int, _: Int) => y + offset

  def alignBottom(offset: Int): RelativeCoord =
    (_: Int, _: Int, _: Int, h: Int) => h + offset

  def horizontalCenterMin(width: Int): RelativeCoord =
    (x: Int, _: Int, w: Int, _: Int) => x + w / 2 - width / 2

  def horizontalCenterMax(width: Int): RelativeCoord =
    (x: Int, _: Int, w: Int, _: Int) => x + w / 2 + math.ceil(width.toDouble / 2).toInt

  def verticalCenterMin(height: Int): RelativeCoord =
    (_: Int, y: Int, _: Int, h: Int) => y + h / 2 - height / 2

  def verticalCenterMax(height: Int): RelativeCoord =
    (_: Int, y: Int, _: Int, h: Int) => y + h / 2 + math.ceil(height.toDouble / 2).toInt


  private implicit def fromFunction(f: (Int, Int, Int, Int) => Int): RelativeCoord = new RelativeCoord {
    override def absoluteCoord(x: Int, y: Int, w: Int, h: Int): Int = f(x, y, w, h)
  }
}
