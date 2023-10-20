package hohserg.dimensional.layers.gui.preset.list

import hohserg.dimensional.layers.gui.RelativeCoord

import java.awt.Rectangle

case class DrawableArea(minX: RelativeCoord, minY: RelativeCoord, maxX: RelativeCoord, maxY: RelativeCoord, uv: (Double, Double, Double, Double), hoveringUV: (Double, Double, Double, Double))

object DrawableArea {
  def apply(minX: RelativeCoord, minY: RelativeCoord, maxX: RelativeCoord, maxY: RelativeCoord, uv: Rectangle): DrawableArea =
    DrawableArea(
      minX, minY, maxX, maxY,
      (uv.x / 256d, uv.y / 256d, (uv.x + uv.width) / 256d, (uv.y + uv.height) / 256d),
      ((uv.x + uv.width + 2) / 256d, uv.y / 256d, (uv.x + uv.width + uv.width + 2) / 256d, (uv.y + uv.height) / 256d)
    )

  def apply(area: Rectangle, uv: Rectangle): DrawableArea =
    DrawableArea(
      RelativeCoord.alignLeft(area.x), RelativeCoord.alignTop(area.y),
      RelativeCoord.alignLeft(area.x + area.width), RelativeCoord.alignTop(area.y + area.height),
      uv
    )
}
