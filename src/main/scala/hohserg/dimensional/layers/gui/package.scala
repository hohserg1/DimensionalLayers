package hohserg.dimensional.layers

import hohserg.dimensional.layers.gui.DrawableArea.Container
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.client.resources.I18n
import net.minecraft.world.{DimensionType, WorldType}
import org.lwjgl.opengl.GL11

package object gui {

  def makeDimensionTypeLabel(dimensionType: DimensionType): String =
    dimensionType.getName

  def makeWorldTypeLabel(worldType: WorldType): String =
    I18n.format("selectWorld.mapType") + " " + I18n.format(worldType.getTranslationKey)

  def drawHighlightHovering(area: DrawableArea)(implicit container: Container): Unit = {
    drawHighlightHovering(area.x, area.y, area.w, area.h)
  }

  def drawHighlightHovering(xx: Int, yy: Int, ww: Int, hh: Int): Unit = {
    drawHighlight(xx, yy, ww, hh, 136, 146, 201)
  }

  def drawHighlight(xx: Int, yy: Int, ww: Int, hh: Int, red: Int = 255, green: Int = 255, blue: Int = 255): Unit = {
    GlStateManager.disableTexture2D()
    val z = -100
    val tess = Tessellator.getInstance()
    val buffer = tess.getBuffer
    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)

    buffer.pos(xx - 1, yy, z).color(red, green, blue, 255).endVertex()
    buffer.pos(xx + ww + 1, yy, z).color(red, green, blue, 255).endVertex()
    buffer.pos(xx + ww + 1, yy - 1, z).color(red, green, blue, 255).endVertex()
    buffer.pos(xx - 1, yy - 1, z).color(red, green, blue, 255).endVertex()


    buffer.pos(xx - 1, yy + hh + 1, z).color(red, green, blue, 255).endVertex()
    buffer.pos(xx, yy + hh + 1, z).color(red, green, blue, 255).endVertex()
    buffer.pos(xx, yy - 1, z).color(red, green, blue, 255).endVertex()
    buffer.pos(xx - 1, yy - 1, z).color(red, green, blue, 255).endVertex()


    buffer.pos(xx + ww, yy - 1, z).color(red, green, blue, 255).endVertex()
    buffer.pos(xx + ww, yy + hh + 1, z).color(red, green, blue, 255).endVertex()
    buffer.pos(xx + ww + 1, yy + hh + 1, z).color(red, green, blue, 255).endVertex()
    buffer.pos(xx + ww + 1, yy - 1, z).color(red, green, blue, 255).endVertex()


    buffer.pos(xx - 1, yy + hh + 1, z).color(red, green, blue, 255).endVertex()
    buffer.pos(xx + ww + 1, yy + hh + 1, z).color(red, green, blue, 255).endVertex()
    buffer.pos(xx + ww + 1, yy + hh, z).color(red, green, blue, 255).endVertex()
    buffer.pos(xx - 1, yy + hh, z).color(red, green, blue, 255).endVertex()

    tess.draw()
    GlStateManager.enableTexture2D()
  }

}
