package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.gui.DrawableArea.Container
import net.minecraft.client.renderer.BufferBuilder

import java.awt.Rectangle

case class DrawableArea(minX: RelativeCoord, minY: RelativeCoord, maxX: RelativeCoord, maxY: RelativeCoord, uv: (Double, Double, Double, Double), hoveringUV: (Double, Double, Double, Double)) {
  def isHovering(implicit container: Container): Boolean = {
    DrawableArea.isHovering(
      minX.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY),
      minY.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY),
      maxX.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY),
      maxY.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY)
    )
  }

  def draw(buffer: BufferBuilder)(implicit container: Container): Unit = {
    val areaMinX = minX.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY)
    val areaMinY = minY.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY)
    val areaMaxX = maxX.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY)
    val areaMaxY = maxY.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY)
    DrawableArea.drawRect(
      buffer,
      areaMinX, areaMinY, areaMaxX, areaMaxY,
      if (isHovering) hoveringUV else uv
    )
  }
}

object DrawableArea {
  def apply(minX: RelativeCoord, minY: RelativeCoord, maxX: RelativeCoord, maxY: RelativeCoord, uv: Rectangle): DrawableArea =
    DrawableArea(
      minX, minY, maxX, maxY,
      uv,
      new Rectangle(uv.x + uv.width + 2, uv.y, uv.width, uv.height)
    )

  def apply(minX: RelativeCoord, minY: RelativeCoord, maxX: RelativeCoord, maxY: RelativeCoord, uv: Rectangle, hoveringUV: Rectangle): DrawableArea =
    DrawableArea(
      minX, minY, maxX, maxY,
      (uv.x / 256d, uv.y / 256d, (uv.x + uv.width) / 256d, (uv.y + uv.height) / 256d),
      (hoveringUV.x / 256d, hoveringUV.y / 256d, (hoveringUV.x + hoveringUV.width) / 256d, (hoveringUV.y + hoveringUV.height) / 256d)
    )

  def apply(area: Rectangle, uv: Rectangle): DrawableArea =
    DrawableArea(
      RelativeCoord.alignLeft(area.x), RelativeCoord.alignTop(area.y),
      RelativeCoord.alignLeft(area.x + area.width), RelativeCoord.alignTop(area.y + area.height),
      uv
    )

  def apply(area: Rectangle, uv: Rectangle, hoveringUV: Rectangle): DrawableArea =
    DrawableArea(
      RelativeCoord.alignLeft(area.x), RelativeCoord.alignTop(area.y),
      RelativeCoord.alignLeft(area.x + area.width), RelativeCoord.alignTop(area.y + area.height),
      uv, hoveringUV
    )

  trait Container {
    def minX: Int

    def minY: Int

    def maxX: Int

    def maxY: Int
  }

  def isHovering(minX: Int, minY: Int, maxX: Int, maxY: Int): Boolean = {
    val (mouseX, mouseY) = MouseUtils.getMousePos
    minX <= mouseX && mouseX < maxX &&
      minY <= mouseY && mouseY < maxY
  }

  def drawRect(buffer: BufferBuilder,
               areaMinX: Int, areaMinY: Int, areaMaxX: Int, areaMaxY: Int,
               uv: (Double, Double, Double, Double)): Unit = {
    val z = 0;

    val (u1, v1, u2, v2) = uv

    buffer.pos(areaMinX, areaMaxY, z).tex(u1, v2).endVertex()
    buffer.pos(areaMaxX, areaMaxY, z).tex(u2, v2).endVertex()
    buffer.pos(areaMaxX, areaMinY, z).tex(u2, v1).endVertex()
    buffer.pos(areaMinX, areaMinY, z).tex(u1, v1).endVertex()
  }
}
