package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.gui.preset.list.texture
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import org.lwjgl.opengl.GL11

import java.awt.Rectangle

class GuiTexturedButton(x: Int, y: Int, w: Int, h: Int, label: String, standardUV: Rectangle)(clicked: Handler)(implicit gui: GuiBase)
  extends GuiClickableButton(x, y, w, h, label)(clicked)
    with DrawableArea.Container {
  val area = DrawableArea(
    RelativeCoord.alignLeft(0), RelativeCoord.alignTop(0),
    RelativeCoord.alignRight(0), RelativeCoord.alignBottom(0),
    standardUV
  )

  implicit def self: DrawableArea.Container = this

  override def drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    if (visible) {
      GlStateManager.color(1f, 1, 1, 1)
      mc.getTextureManager.bindTexture(texture)

      val tess = Tessellator.getInstance()
      val buffer = tess.getBuffer
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

      area.draw(buffer)

      tess.draw()

      drawCenteredString(mc.fontRenderer, displayString, x + w / 2, y + (h - 8) / 2, 0xE0E0E0)
    }
  }

  override def minX: Int = x

  override def minY: Int = y

  override def maxX: Int = x + width

  override def maxY: Int = y + height
}
