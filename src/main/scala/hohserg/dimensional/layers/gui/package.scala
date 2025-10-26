package hohserg.dimensional.layers

import hohserg.dimensional.layers.gui.DrawableArea.Container
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{BufferBuilder, GlStateManager, Tessellator}
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation
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
    GlStateManager.disableDepth()
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

  def drawWithTexture(texture: ResourceLocation, render: BufferBuilder => Unit): Unit = {
    val tess = Tessellator.getInstance()
    Minecraft.getMinecraft.getTextureManager.bindTexture(texture)

    val buffer: BufferBuilder = tess.getBuffer
    buffer.begin(7, DefaultVertexFormats.POSITION_TEX)
    render(buffer)
    tess.draw()
  }

  def drawTexturedRect(minX: Int, minY: Int, maxX: Int, maxY: Int, texture: ResourceLocation, u1: Double, v1: Double, u2: Double, v2: Double): Unit = {
    drawWithTexture(texture, buffer => {
      val z = -100
      buffer.pos(minX, minY, z).tex(u1, v1).endVertex()
      buffer.pos(minX, maxY, z).tex(u1, v2).endVertex()
      buffer.pos(maxX, maxY, z).tex(u2, v2).endVertex()
      buffer.pos(maxX, minY, z).tex(u2, v1).endVertex()
    })
  }

}
