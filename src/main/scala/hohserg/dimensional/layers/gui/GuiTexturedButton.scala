package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.gui.preset.list.texture
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import org.lwjgl.opengl.GL11

import java.awt.Rectangle

class GuiTexturedButton(x: Int, y: Int, w: Int, h: Int, label: String, uv: Rectangle)(clicked: Handler)(implicit gui: GuiBase)
  extends GuiClickableButton(x, y, w, h, label)(clicked)
    with DrawableArea.Container {

  val area = DrawableArea(
    RelativeCoord.alignLeft(0), RelativeCoord.alignTop(0),
    RelativeCoord.alignRight(0), RelativeCoord.alignBottom(0),
    uv
  )

  val (unhoverAreasToDraw: Seq[DrawableArea], hoverAreasToDraw: Seq[DrawableArea]) =
    if (w > h * 2) {
      val leftArea = DrawableArea(
        RelativeCoord.alignLeft(0), RelativeCoord.alignTop(0),
        RelativeCoord.alignLeft(h), RelativeCoord.alignBottom(0),
        new Rectangle(uv.x, uv.y, uv.height, uv.height),
        new Rectangle(uv.x, uv.y, uv.height, uv.height)
      )
      val centerArea = DrawableArea(
        RelativeCoord.alignLeft(h), RelativeCoord.alignTop(0),
        RelativeCoord.alignRight(-h), RelativeCoord.alignBottom(0),
        new Rectangle(uv.x + uv.height, uv.y, uv.width - uv.height - uv.height, uv.height),
        new Rectangle(uv.x + uv.height, uv.y, uv.width - uv.height - uv.height, uv.height)
      )
      val rightArea = DrawableArea(
        RelativeCoord.alignRight(-h), RelativeCoord.alignTop(0),
        RelativeCoord.alignRight(0), RelativeCoord.alignBottom(0),
        new Rectangle(uv.x + uv.width - uv.height, uv.y, uv.height, uv.height),
        new Rectangle(uv.x + uv.width - uv.height, uv.y, uv.height, uv.height)
      )

      val leftAreaHovering = DrawableArea(
        RelativeCoord.alignLeft(0), RelativeCoord.alignTop(0),
        RelativeCoord.alignLeft(h), RelativeCoord.alignBottom(0),
        new Rectangle(uv.x + uv.width + 2, uv.y, uv.height, uv.height),
        new Rectangle(uv.x + uv.width + 2, uv.y, uv.height, uv.height)
      )
      val centerAreaHovering = DrawableArea(
        RelativeCoord.alignLeft(h), RelativeCoord.alignTop(0),
        RelativeCoord.alignRight(-h), RelativeCoord.alignBottom(0),
        new Rectangle(uv.x + uv.height + uv.width + 2, uv.y, uv.width - uv.height - uv.height, uv.height),
        new Rectangle(uv.x + uv.height + uv.width + 2, uv.y, uv.width - uv.height - uv.height, uv.height)
      )
      val rightAreaHovering = DrawableArea(
        RelativeCoord.alignRight(-h), RelativeCoord.alignTop(0),
        RelativeCoord.alignRight(0), RelativeCoord.alignBottom(0),
        new Rectangle(uv.x + uv.width - uv.height + uv.width + 2, uv.y, uv.height, uv.height),
        new Rectangle(uv.x + uv.width - uv.height + uv.width + 2, uv.y, uv.height, uv.height)
      )
      Seq(leftArea, centerArea, rightArea) -> Seq(leftAreaHovering, centerAreaHovering, rightAreaHovering)
    } else {
      Seq(area) -> Seq(area)
    }

  implicit def self: DrawableArea.Container = this

  override def drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    if (visible) {
      GlStateManager.color(1f, 1, 1, 1)
      mc.getTextureManager.bindTexture(texture)

      val tess = Tessellator.getInstance()
      val buffer = tess.getBuffer
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

      if (area.isHovering)
        hoverAreasToDraw.foreach(_.draw(buffer))
      else
        unhoverAreasToDraw.foreach(_.draw(buffer))

      tess.draw()

      drawCenteredString(mc.fontRenderer, displayString, x + w / 2, y + (h - 8) / 2, 0xE0E0E0)
    }
  }

  override def minX: Int = x

  override def minY: Int = y

  override def maxX: Int = x + width

  override def maxY: Int = y + height
}
