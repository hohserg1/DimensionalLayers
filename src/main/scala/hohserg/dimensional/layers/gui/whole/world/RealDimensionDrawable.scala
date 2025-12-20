package hohserg.dimensional.layers.gui.whole.world

import hohserg.dimensional.layers.gui.preset.list.{background, texture}
import hohserg.dimensional.layers.gui.settings.mystcraft.SymbolDrawable
import hohserg.dimensional.layers.gui.settings.solid.GuiBlocksList.DrawableBlock
import hohserg.dimensional.layers.gui.{Drawable, DrawableArea, IconUtils, RelativeCoord, drawHighlight}
import hohserg.dimensional.layers.preset.SingleDimensionPreset
import hohserg.dimensional.layers.preset.spec.{CubicWorldTypeLayerSpec, DimensionLayerSpec, MystcraftLayerSpec, SolidLayerSpec}
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.world.{DimensionType, WorldType}
import org.lwjgl.opengl.GL11

import java.awt.Rectangle
import scala.annotation.nowarn

val remove = DrawableArea(
  RelativeCoord.alignRight(-5 - 13), RelativeCoord.alignBottom(-5 - 12),
  RelativeCoord.alignRight(-5), RelativeCoord.alignBottom(-5),
  new Rectangle(2, 38, 13, 12)
)

sealed trait RealDimensionListElement extends Drawable

case object AddNew extends RealDimensionListElement {
  override def draw(minX: Int, minY: Int, maxX: Int, maxY: Int): Unit = {
    val fullW = maxX - minX - 4
    drawHighlight(minX + 2, minY + 2, fullW, fullW, 100, 255, 100)
    Minecraft.getMinecraft.fontRenderer.drawString("+", minX + 4, minY + 4, 0xff_FF00FF, true)
  }

  override def tooltip: String = "add layers to one more real dimension"
}

case class RealDimensionDrawable(id: Int, preset: SingleDimensionPreset) extends RealDimensionListElement with DrawableArea.Container {

  implicit def self: DrawableArea.Container = this

  val reversed = preset.layers.reverse.map {
    case spec: DimensionLayerSpec =>
      spec.dimensionType
    case spec: CubicWorldTypeLayerSpec =>
      spec.cubicWorldType
    case spec: SolidLayerSpec =>
      DrawableBlock(spec.filler.getBlock)
    case spec:MystcraftLayerSpec=>
      SymbolDrawable(spec.symbols.head)
  }.zipWithIndex

  @nowarn("msg=with")
  override def draw(minX: Int, minY: Int, maxX: Int, maxY: Int): Unit = {
    this.minX = minX
    this.minY = minY
    this.maxX = maxX
    this.maxY = maxY

    val fullW = maxX - minX - 4
    val scale = math.min(0.8, 1.7 / preset.layers.size)
    val w = (fullW * scale).toInt
    val step = (fullW - w) / math.max(1, preset.layers.size - 1)
    reversed.foreach { case (l, i) =>
      val x = minX + 2 + step * i
      val y = maxY - 2 - w - step * i
      l match {
        case dimensionType: DimensionType =>
          IconUtils.drawLogo(dimensionType, x, y, w)
        case cubicWorldType: WorldType with ICubicWorldType =>
          IconUtils.drawLogo(cubicWorldType, x, y, w)
        case block: DrawableBlock =>
          block.draw(x, y, x + w, y + w)
      }
    }

    drawHighlight(minX + 2, minY + 2, fullW, fullW, 100, 255, 100)

    Minecraft.getMinecraft.getTextureManager.bindTexture(texture)

    val tess = Tessellator.getInstance()
    val buffer = tess.getBuffer
    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

    if (background.isHovering) {
      remove.draw(buffer)
    }

    tess.draw()

    Minecraft.getMinecraft.fontRenderer.drawString(id.toString, minX + 4, minY + 4, 0xff_FF00FF, true)
  }

  override def tooltip: String = {
    if (remove.isHovering)
      "remove layers of real dimension " + id
    else
      "edit layers of real dimension " + id
  }

  var minX: Int = 0
  var minY: Int = 0
  var maxX: Int = 0
  var maxY: Int = 0
}
