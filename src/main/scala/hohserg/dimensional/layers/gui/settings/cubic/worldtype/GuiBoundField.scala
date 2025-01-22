package hohserg.dimensional.layers.gui.settings.cubic.worldtype

import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer.texture
import hohserg.dimensional.layers.gui.{DrawableArea, GuiBase, GuiCubeYField}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import org.lwjgl.opengl.GL11

import java.awt.Rectangle
import scala.util.Try

class GuiBoundField(x: Int, value: ValueHolder[Int], isMax: Boolean)
                   (implicit gui: GuiBase)
  extends GuiCubeYField(x, 0, value)
    with DrawableArea.Container {

  setEnableBackgroundDrawing(false)

  val label: String = if (isMax) "max cube" else "min cube"

  val max = DrawableArea(
    new Rectangle(-96, -4, 154, 21),
    new Rectangle(37, 72, 154, 21),
    new Rectangle(37, 72, 154, 21)
  )
  val min = DrawableArea(
    new Rectangle(-96, -8, 154, 21),
    new Rectangle(37, 96, 154, 21),
    new Rectangle(37, 96, 154, 21)
  )
  val area = if (isMax) max else min

  implicit def self: DrawableArea.Container = this

  override def minX: Int = x

  override def minY: Int = y

  override def maxX: Int = x + width

  override def maxY: Int = y + height

  override def drawTextBox(): Unit = {
    GlStateManager.color(1, 1, 1, 1)
    Minecraft.getMinecraft.getTextureManager.bindTexture(texture)
    val tess = Tessellator.getInstance()
    val buffer = tess.getBuffer
    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
    area.draw(buffer)
    tess.draw()

    super.drawTextBox()

    drawString(fontRenderer, label, x - fontRenderer.getStringWidth(label) - 24, y, 0xffa0a0a0)
  }

  override def setText(textIn: String): Unit = {
    if (isFocused)
      Try(textIn.toInt).foreach { nv =>
        value.set(nv)
        updateVisual(nv)
      }
    else
      super.setText(textIn)
  }
}
