package hohserg.dimensional.layers.gui.preset.list

import hohserg.dimensional.layers.DimensionLayersPreset.LayerSpec
import hohserg.dimensional.layers.Main
import hohserg.dimensional.layers.gui.preset.list.GuiLayerEntry._
import hohserg.dimensional.layers.gui.{DimensionClientUtils, RelativeCoord}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{BufferBuilder, Tessellator}
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

import java.awt.Rectangle


object GuiLayerEntry {
  val texture = new ResourceLocation(Main.modid, "textures/gui/layer_entry.png")

  val moveUp = DrawableArea(
    RelativeCoord.alignLeft(DimensionClientUtils.width + 4), RelativeCoord.alignTop(4),
    RelativeCoord.alignLeft(DimensionClientUtils.width + 4 + 26), RelativeCoord.alignTop(4 + 16),
    new Rectangle(2, 2, 26, 16)
  )
  val moveDown = DrawableArea(
    RelativeCoord.alignLeft(DimensionClientUtils.width + 4), RelativeCoord.alignBottom(-4 - 16),
    RelativeCoord.alignLeft(DimensionClientUtils.width + 4 + 26), RelativeCoord.alignBottom(-4),
    new Rectangle(2, 20, 26, 16)
  )
  val remove = DrawableArea(
    RelativeCoord.alignRight(-4 - 13), RelativeCoord.alignTop(4),
    RelativeCoord.alignRight(-4), RelativeCoord.alignTop(4 + 12),
    new Rectangle(2, 38, 13, 12)
  )

  val background = DrawableArea(
    RelativeCoord.alignLeft(0), RelativeCoord.alignTop(0),
    RelativeCoord.alignRight(0), RelativeCoord.alignBottom(0),
    new Rectangle(150, 0, 1, 1)
  )

  val settings = DrawableArea(
    RelativeCoord.alignRight(-20 - 40), RelativeCoord.alignTop(22),
    RelativeCoord.alignRight(-40), RelativeCoord.alignTop(22 + 20),
    new Rectangle(2, 52, 20, 20)
  )

}

trait GuiLayerEntry {
  def parent: GuiLayersList

  def layer: LayerSpec

  protected val mc = Minecraft.getMinecraft

  protected var minX: Int = 0
  protected var minY: Int = 0
  protected var maxX: Int = 0
  protected var maxY: Int = 0
  protected var mouseX: Int = 0
  protected var mouseY: Int = 0

  def drawEntry(index: Int, minX: Int, minY: Int, maxX: Int, maxY: Int, mouseX: Int, mouseY: Int): Unit = {
    this.minX = minX
    this.minY = minY
    this.maxX = maxX
    this.maxY = maxY
    this.mouseX = mouseX
    this.mouseY = mouseY

    if (isHovering(background)) {
      mc.getTextureManager.bindTexture(GuiLayerEntry.texture)

      val tess = Tessellator.getInstance()
      val buffer = tess.getBuffer

      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

      drawBackground(buffer)

      drawMoveUp(index, buffer)
      drawMoveDown(index, buffer)
      drawRemove(buffer)
      drawSettings(buffer)

      tess.draw()
    }
  }

  def drawBackground(buffer: BufferBuilder): Unit =
    drawArea(background, buffer)

  def drawMoveUp(index: Int, buffer: BufferBuilder): Unit =
    if (isNotFirst(index))
      drawArea(moveUp, buffer)

  def drawMoveDown(index: Int, buffer: BufferBuilder): Unit =
    if (isNotLast(index))
      drawArea(moveDown, buffer)

  def drawRemove(buffer: BufferBuilder): Unit = {
    drawArea(remove, buffer)
  }

  def drawSettings(buffer: BufferBuilder): Unit = {
    drawArea(settings, buffer)
  }

  private def isHovering(area: DrawableArea): Boolean = isHovering(
    area.minX.absoluteCoord(minX, minY, maxX, maxY),
    area.minY.absoluteCoord(minX, minY, maxX, maxY),
    area.maxX.absoluteCoord(minX, minY, maxX, maxY),
    area.maxY.absoluteCoord(minX, minY, maxX, maxY)
  )

  private def isHovering(minX: Int, minY: Int, maxX: Int, maxY: Int) =
    minX <= mouseX && mouseX < maxX &&
      minY <= mouseY && mouseY < maxY

  private def drawArea(area: DrawableArea, buffer: BufferBuilder): Unit = {
    val areaMinX = area.minX.absoluteCoord(minX, minY, maxX, maxY)
    val areaMinY = area.minY.absoluteCoord(minX, minY, maxX, maxY)
    val areaMaxX = area.maxX.absoluteCoord(minX, minY, maxX, maxY)
    val areaMaxY = area.maxY.absoluteCoord(minX, minY, maxX, maxY)
    drawRect(
      buffer,
      areaMinX, areaMinY, areaMaxX, areaMaxY,
      if (isHovering(areaMinX, areaMinY, areaMaxX, areaMaxY)) area.hoveringUV else area.uv
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

  def clicked(index: Int, mouseX: Int, mouseY: Int): Unit = {
    checkMoveUpClicked(index, mouseX, mouseY)
    checkMoveDownClicked(index, mouseX, mouseY)
    checkRemoveClicked(index, mouseX, mouseY)
    checkSettingsClicked(index, mouseX, mouseY)
  }

  private def checkMoveUpClicked(index: Int, mouseX: Int, mouseY: Int): Unit = {
    if (isNotFirst(index)) {
      if (isHovering(moveUp)) {
        val prev = parent.entries(index - 1)
        parent.entries(index - 1) = this
        parent.entries(index) = prev
        parent.scrollUpOnce();
      }
    }
  }

  private def checkMoveDownClicked(index: Int, mouseX: Int, mouseY: Int): Unit = {
    if (isNotLast(index)) {
      if (isHovering(moveDown)) {
        val next = parent.entries(index + 1)
        parent.entries(index + 1) = this
        parent.entries(index) = next
        parent.scrollDownOnce();
      }
    }
  }

  private def checkRemoveClicked(index: Int, mouseX: Int, mouseY: Int): Unit = {
    if (isHovering(remove)) {
      parent.entries.remove(index)
    }
  }

  private def checkSettingsClicked(index: Int, mouseX: Int, mouseY: Int): Unit = {
    if (isHovering(settings)) {
    }
  }

  private def isNotLast(index: Int) =
    index != parent.entries.size - 1

  private def isNotFirst(index: Int) =
    index != 0
}
