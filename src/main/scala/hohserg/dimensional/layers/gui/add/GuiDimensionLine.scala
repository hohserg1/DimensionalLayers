package hohserg.dimensional.layers.gui.add

import hohserg.dimensional.layers.gui.DimensionLogo
import hohserg.dimensional.layers.gui.add.GuiDimensionLine.offset
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.world.DimensionType
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11

object GuiDimensionLine {
  final val offset = (GuiDimensionList.slotWidth - DimensionLogo.width) / 2
}

class GuiDimensionLine(parent: GuiDimensionList, val dims: Seq[DimensionType]) {


  def drawEntry(x: Int, y: Int, mouseX: Int, mouseY: Int): Unit = {
    for (i <- dims.indices) {
      val dimensionType = dims(i)
      val xx = x + i * GuiDimensionList.slotWidth + GuiDimensionLine.offset
      val yy = y + offset
      DimensionLogo.draw(dimensionType, xx, yy)

      if (xx <= mouseX && mouseX < xx + DimensionLogo.width &&
        yy <= mouseY && mouseY < yy + DimensionLogo.width) {
        drawHighlight(xx, yy)
        if (Mouse.isButtonDown(0)) {
          parent.parent.select(dimensionType)
        }
      }
    }
  }

  def drawHighlight(xx: Int, yy: Int): Unit = {
    GlStateManager.disableTexture2D()
    val z = -100
    val tess = Tessellator.getInstance()
    val buffer = tess.getBuffer
    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)

    buffer.pos(xx - 1, yy, z).color(1, 1, 1, 1f).endVertex()
    buffer.pos(xx + DimensionLogo.width + 1, yy, z).color(1, 1, 1, 1f).endVertex()
    buffer.pos(xx + DimensionLogo.width + 1, yy - 1, z).color(1, 1, 1, 1f).endVertex()
    buffer.pos(xx - 1, yy - 1, z).color(1, 1, 1, 1f).endVertex()


    buffer.pos(xx - 1, yy + DimensionLogo.width + 1, z).color(1, 1, 1, 1f).endVertex()
    buffer.pos(xx, yy + DimensionLogo.width + 1, z).color(1, 1, 1, 1f).endVertex()
    buffer.pos(xx, yy - 1, z).color(1, 1, 1, 1f).endVertex()
    buffer.pos(xx - 1, yy - 1, z).color(1, 1, 1, 1f).endVertex()


    buffer.pos(xx + DimensionLogo.width, yy - 1, z).color(1, 1, 1, 1f).endVertex()
    buffer.pos(xx + DimensionLogo.width, yy + DimensionLogo.width + 1, z).color(1, 1, 1, 1f).endVertex()
    buffer.pos(xx + DimensionLogo.width + 1, yy + DimensionLogo.width + 1, z).color(1, 1, 1, 1f).endVertex()
    buffer.pos(xx + DimensionLogo.width + 1, yy - 1, z).color(1, 1, 1, 1f).endVertex()


    buffer.pos(xx - 1, yy + DimensionLogo.width + 1, z).color(1, 1, 1, 1f).endVertex()
    buffer.pos(xx + DimensionLogo.width + 1, yy + DimensionLogo.width + 1, z).color(1, 1, 1, 1f).endVertex()
    buffer.pos(xx + DimensionLogo.width + 1, yy + DimensionLogo.width, z).color(1, 1, 1, 1f).endVertex()
    buffer.pos(xx - 1, yy + DimensionLogo.width, z).color(1, 1, 1, 1f).endVertex()

    tess.draw()
    GlStateManager.enableTexture2D()
  }

  def mousePressed(slotIndex: Int, mouseX: Int, mouseY: Int, mouseEvent: Int, relativeX: Int, relativeY: Int): Boolean = {

    false
  }
}
