package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.Memoized
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{BufferBuilder, Tessellator}
import net.minecraft.util.ResourceLocation
import net.minecraft.world.DimensionType

object DimensionClientUtils {

  def getDisplayName(dimensionType: DimensionType): String =
    dimensionType.getName

  final val width = 64


  def drawLogo(dimensionType: DimensionType, x: Int, y: Int): Unit = {
    val tess = Tessellator.getInstance()
    Minecraft.getMinecraft.getTextureManager.bindTexture(getBackgroundForDimensionType(dimensionType))

    val buffer: BufferBuilder = tess.getBuffer
    buffer.begin(7, DefaultVertexFormats.POSITION_TEX)
    val z = -100
    buffer.pos(x, y, z).tex(0, 0).endVertex()
    buffer.pos(x, y + width, z).tex(0, 1).endVertex()
    buffer.pos(x + width, y + width, z).tex(1, 1).endVertex()
    buffer.pos(x + width, y, z).tex(1, 0).endVertex()
    tess.draw()
  }


  val missingBg = new ResourceLocation("textures/gui/dimension_layers_background/missing.png")

  val getBackgroundForDimensionType: DimensionType => ResourceLocation =
    Memoized((dimensionType: DimensionType) => {
      val Array(modid, dimName) = ResourceLocation.splitObjectName(dimensionType.getName)
      val bgLocation = new ResourceLocation(modid, "textures/gui/dimension_layers_background/" + dimName + ".png")

      try {
        if (Minecraft.getMinecraft.getResourceManager.getResource(bgLocation) != null)
          bgLocation
        else
          missingBg
      } catch {
        case exception: Exception =>
          missingBg
      }
    })

}
