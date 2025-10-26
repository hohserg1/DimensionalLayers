package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.*
import hohserg.dimensional.layers.gui.DrawableArea.Container
import net.minecraft.client.renderer.BufferBuilder
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import java.awt.Rectangle

@SideOnly(Side.CLIENT)
case class DrawableArea(minX: RelativeCoord, minY: RelativeCoord, maxX: RelativeCoord, maxY: RelativeCoord, uv: (Double, Double, Double, Double), hoveringUV: (Double, Double, Double, Double)) {
  def isHovering(implicit container: Container): Boolean = {
    DrawableArea.isHovering(
      minX.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY),
      minY.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY),
      maxX.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY),
      maxY.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY)
    )
  }

  def x(implicit container: Container): Int = minX.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY)

  def y(implicit container: Container): Int = minY.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY)

  def x2(implicit container: Container): Int = maxX.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY)

  def y2(implicit container: Container): Int = maxY.absoluteCoord(container.minX, container.minY, container.maxX, container.maxY)

  def w(implicit container: Container): Int = x2 - x

  def h(implicit container: Container): Int = y2 - y

  def draw(buffer: BufferBuilder)(implicit container: Container): Unit = {
    drawRect(
      buffer,
      x, y, x2, y2,
      if (isHovering) hoveringUV else uv
    )
  }

  private def drawRect(buffer: BufferBuilder,
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

@SideOnly(Side.CLIENT)
object DrawableArea {
  def apply(minX: RelativeCoord, minY: RelativeCoord, maxX: RelativeCoord, maxY: RelativeCoord, uv: Rectangle): DrawableArea = {
    DrawableArea(
      minX, minY, maxX, maxY,
      uv,
      new Rectangle(uv.x + uv.width + 2, uv.y, uv.width, uv.height)
    )
  }

  def apply(minX: RelativeCoord, minY: RelativeCoord, maxX: RelativeCoord, maxY: RelativeCoord, uv: Rectangle, sameHoveringUV: Boolean): DrawableArea = {
    if (sameHoveringUV)
      DrawableArea(minX, minY, maxX, maxY, uv, uv)
    else
      DrawableArea(minX, minY, maxX, maxY, uv)
  }

  def apply(minX: RelativeCoord, minY: RelativeCoord, maxX: RelativeCoord, maxY: RelativeCoord, uv: Rectangle, hoveringUV: Rectangle): DrawableArea = {
    DrawableArea(
      minX, minY, maxX, maxY,
      (uv.x / 256d, uv.y / 256d, (uv.x + uv.width) / 256d, (uv.y + uv.height) / 256d),
      (hoveringUV.x / 256d, hoveringUV.y / 256d, (hoveringUV.x + hoveringUV.width) / 256d, (hoveringUV.y + hoveringUV.height) / 256d)
    )
  }

  def apply(area: Rectangle, uv: Rectangle): DrawableArea = {
    DrawableArea(
      RelativeCoord.alignLeft(area.x), RelativeCoord.alignTop(area.y),
      RelativeCoord.alignLeft(area.x + area.width), RelativeCoord.alignTop(area.y + area.height),
      uv
    )
  }

  def apply(area: Rectangle, uv: Rectangle, hoveringUV: Rectangle): DrawableArea = {
    DrawableArea(
      RelativeCoord.alignLeft(area.x), RelativeCoord.alignTop(area.y),
      RelativeCoord.alignLeft(area.x + area.width), RelativeCoord.alignTop(area.y + area.height),
      uv, hoveringUV
    )
  }

  trait Container {
    def minX: Int

    def minY: Int

    def maxX: Int

    def maxY: Int
  }

  case class DumbContainer(minX: Int, minY: Int, maxX: Int, maxY: Int) extends Container
  
  class MutableContainer extends Container{
    var minX: Int = 0

    var minY: Int = 0

    var maxX: Int = 0

    var maxY: Int = 0
  }

  def isHovering(minX: Int, minY: Int, maxX: Int, maxY: Int): Boolean = {
    val (mouseX, mouseY) = MouseUtils.getMousePos
    minX <= mouseX && mouseX < maxX &&
      minY <= mouseY && mouseY < maxY
  }
}
