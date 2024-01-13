package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.{Main, Memoized}
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{BufferBuilder, Tessellator}
import net.minecraft.util.ResourceLocation
import net.minecraft.world.{DimensionType, WorldType}

import java.io.FileNotFoundException

object IconUtils {

  final val width = 64

  def drawLogo[A](v: A, x: Int, y: Int, w: Int = width)(implicit getTexture: A => ResourceLocation): Unit = {
    val tess = Tessellator.getInstance()
    Minecraft.getMinecraft.getTextureManager.bindTexture(getTexture(v))

    val buffer: BufferBuilder = tess.getBuffer
    buffer.begin(7, DefaultVertexFormats.POSITION_TEX)
    val z = -100
    buffer.pos(x, y, z).tex(0, 0).endVertex()
    buffer.pos(x, y + w, z).tex(0, 1).endVertex()
    buffer.pos(x + w, y + w, z).tex(1, 1).endVertex()
    buffer.pos(x + w, y, z).tex(1, 0).endVertex()
    tess.draw()
  }

  implicit val getBackgroundForDimensionType: DimensionType => ResourceLocation =
    Memoized(regularTexture(_.getName, "textures/gui/dimension_layers_background/"))

  implicit val getBackgroundForCubicWorldType: WorldType with ICubicWorldType => ResourceLocation =
    Memoized(regularTexture(_.getName, "textures/gui/cubic_world_type_layers_background/"))

  private def regularTexture[A](getName: A => String, basePath: String): A => ResourceLocation =
    v => {
      val Array(modid, name) = ResourceLocation.splitObjectName(getName(v))
      getOrElse(modid, basePath, name)
    }

  private def getOrElse(modid: String, basePath: String, name: String): ResourceLocation = {
    val bgLocation = new ResourceLocation(modid, basePath + name + ".png")
    val missingBg = new ResourceLocation(basePath + "missing.png")
    try {
      if (Minecraft.getMinecraft.getResourceManager.getResource(bgLocation) != null)
        bgLocation
      else
        missingBg
    } catch {
      case exception: FileNotFoundException =>
        Main.sided.printWarning("icon not found", exception)
        missingBg

      case exception: Exception =>
        exception.printStackTrace()
        missingBg
    }
  }
}
