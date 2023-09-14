package hohserg.dimensional.layers.gui.preset

import hohserg.dimensional.layers.DimensionLayersPreset.LayerSpec
import hohserg.dimensional.layers.gui.DimensionLogo
import hohserg.dimensional.layers.gui.preset.GuiDimensionLayerEntry.{moveDown, moveUp, texture}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{BufferBuilder, Tessellator}
import org.lwjgl.opengl.GL11

import java.awt.Rectangle

trait GuiLayerEntry {
  def parent: GuiLayersList

  def layer: LayerSpec

  final val height = DimensionLogo.width
  protected val mc = Minecraft.getMinecraft

  protected var x: Int = 0
  protected var y: Int = 0

  def drawEntry(index: Int, x: Int, y: Int, mouseX: Int, mouseY: Int): Unit = {
    this.x = x
    this.y = y

    if (x <= mouseX && mouseX < x + parent.width && y <= mouseY && mouseY < y + height) {
      mc.getTextureManager.bindTexture(texture)

      val tess = Tessellator.getInstance()
      val buffer = tess.getBuffer

      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

      drawMoveUp(index, mouseX, mouseY, buffer)
      drawMoveDown(index, mouseX, mouseY, buffer)

      tess.draw()
    }
  }


  def drawMoveUp(index: Int, mouseX: Int, mouseY: Int, buffer: BufferBuilder): Unit = {
    if (isNotFirst(index)) {
      val z = 0

      val (u, v) = if (isHovering(moveUp, mouseX, mouseY)) (99, 37) else (99, 5)

      drawRect(moveUp, buffer, z, u, v)
    }
  }

  private def isHovering(rect: Rectangle, mouseX: Int, mouseY: Int) =
    x + rect.x <= mouseX && mouseX < x + rect.x + rect.width &&
      y + rect.y <= mouseY && mouseY < y + rect.y + rect.height


  def drawMoveDown(index: Int, mouseX: Int, mouseY: Int, buffer: BufferBuilder): Unit = {
    if (isNotLast(index)) {
      val z = 0

      val (u, v) = if (isHovering(moveDown, mouseX, mouseY)) (67, 52) else (67, 20)

      drawRect(moveDown, buffer, z, u, v)
    }
  }

  private def drawRect(rect: Rectangle, buffer: BufferBuilder, z: Int, u: Int, v: Int): Unit = {
    buffer.pos(x + rect.x, y + rect.y + rect.height, z).tex(u / 256d, (v + rect.height) / 256d).endVertex()
    buffer.pos(x + rect.x + rect.width, y + rect.y + rect.height, z).tex((u + rect.width) / 256d, (v + rect.height) / 256d).endVertex()
    buffer.pos(x + rect.x + rect.width, y + rect.y, z).tex((u + rect.width) / 256d, v / 256d).endVertex()
    buffer.pos(x + rect.x, y + rect.y, z).tex(u / 256d, v / 256d).endVertex()
  }

  def clicked(index: Int, mouseX: Int, mouseY: Int): Unit = {
    checkMoveUpClicked(index, mouseX, mouseY)
    checkMoveDownClicked(index, mouseX, mouseY)
  }

  private def checkMoveUpClicked(index: Int, mouseX: Int, mouseY: Int): Unit = {
    if (isNotFirst(index)) {
      if (isHovering(moveUp, mouseX, mouseY)) {
        val prev = parent.entries(index - 1)
        parent.entries(index - 1) = this
        parent.entries(index) = prev
      }
    }
  }

  private def checkMoveDownClicked(index: Int, mouseX: Int, mouseY: Int): Unit = {
    if (isNotLast(index)) {
      if (isHovering(moveDown, mouseX, mouseY)) {
        val next = parent.entries(index + 1)
        parent.entries(index + 1) = this
        parent.entries(index) = next
      }
    }
  }

  private def isNotLast(index: Int) =
    index != parent.entries.size - 1

  private def isNotFirst(index: Int) =
    index != 0
}
