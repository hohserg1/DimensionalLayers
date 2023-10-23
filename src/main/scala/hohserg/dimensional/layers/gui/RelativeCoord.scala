package hohserg.dimensional.layers.gui

import scala.language.implicitConversions

trait RelativeCoord {
  def absoluteCoord(minX: Int, minY: Int, maxX: Int, maxY: Int): Int
}

object RelativeCoord {

  def alignLeft(offset: Int): RelativeCoord =
    (minX: Int, _: Int, _: Int, _: Int) => minX + offset

  def alignRight(offset: Int): RelativeCoord =
    (_: Int, _: Int, maxX: Int, _: Int) => maxX + offset

  def alignTop(offset: Int): RelativeCoord =
    (_: Int, minY: Int, _: Int, _: Int) => minY + offset

  def alignBottom(offset: Int): RelativeCoord =
    (_: Int, _: Int, _: Int, maxY: Int) => maxY + offset

  def horizontalCenterMin(width: Int): RelativeCoord =
    (minX: Int, _: Int, maxX: Int, _: Int) => minX + (maxX - minX) / 2 - width / 2

  def horizontalCenterMax(width: Int): RelativeCoord =
    (minX: Int, _: Int, maxX: Int, _: Int) => minX + (maxX - minX) / 2 + math.ceil(width.toDouble / 2).toInt

  def verticalCenterMin(height: Int): RelativeCoord =
    (_: Int, minY: Int, _: Int, maxY: Int) => minY + (maxY - minY) / 2 - height / 2

  def verticalCenterMax(height: Int): RelativeCoord =
    (_: Int, minY: Int, _: Int, maxY: Int) => minY + (maxY - minY) / 2 + math.ceil(height.toDouble / 2).toInt


  private implicit def fromFunction(f: (Int, Int, Int, Int) => Int): RelativeCoord = new RelativeCoord {
    override def absoluteCoord(minX: Int, minY: Int, maxX: Int, maxY: Int): Int = f(minX, minY, maxX, maxY)
  }
}
