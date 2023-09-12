package hohserg.dimension.layers.gui.preset

import hohserg.dimension.layers.DimensionLayersPreset
import hohserg.dimension.layers.DimensionLayersPreset.{DimensionLayerSpec, SolidLayerSpec}
import hohserg.dimension.layers.gui.DimensionLogo
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.world.DimensionType
import net.minecraftforge.fml.client.GuiScrollingList

import scala.collection.mutable

class GuiLayersList(parent: GuiSetupDimensionLayersPreset, val width: Int, height: Int, settings: String)
  extends GuiScrollingList(
    Minecraft.getMinecraft,
    width, height, 10, height - 10, 10, DimensionLogo.width + 4,
    Minecraft.getMinecraft.displayWidth, Minecraft.getMinecraft.displayHeight
  ) {

  println(settings)
  println(DimensionLayersPreset(settings).layers)

  val entries: mutable.Buffer[GuiLayerEntry] =
    DimensionLayersPreset(settings).layers
      .map {
        case layer: DimensionLayerSpec => new GuiDimensionLayerEntry(this, layer)
        case layer: SolidLayerSpec => new GuiSolidLayerEntry(this, layer)
      }
      .toBuffer

  def add(dimensionType: DimensionType): Unit = {
    entries += new GuiDimensionLayerEntry(this, DimensionLayerSpec(dimensionType))
  }

  def toSettings: String = DimensionLayersPreset(entries.map(_.layer).toList).toSettings

  override def getSize: Int = entries.size

  override def drawSlot(index: Int, right: Int, top: Int, height: Int, tess: Tessellator): Unit = {
    entries(index).drawEntry(index, left, top, mouseX, mouseY)
  }

  override def elementClicked(index: Int, doubleClick: Boolean): Unit = {
    entries(index).clicked(index, mouseX, mouseY)
  }

  override def isSelected(index: Int): Boolean = false

  override def drawBackground(): Unit = ()
}
