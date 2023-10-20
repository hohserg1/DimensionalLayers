package hohserg.dimensional.layers.gui.preset.list

import hohserg.dimensional.layers.DimensionLayersPreset
import hohserg.dimensional.layers.DimensionLayersPreset.{DimensionLayerSpec, SolidLayerSpec}
import hohserg.dimensional.layers.gui.DimensionClientUtils
import hohserg.dimensional.layers.gui.mixin.AccessorGuiScrollingList
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionLayersPreset
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.world.DimensionType
import net.minecraftforge.fml.client.GuiScrollingList

import scala.collection.mutable

class GuiLayersList(parent: GuiSetupDimensionLayersPreset, val width: Int, height: Int, settings: String)
  extends GuiScrollingList(
    Minecraft.getMinecraft,
    width, height, 10, height - 10, 10, DimensionClientUtils.width + 4,
    Minecraft.getMinecraft.displayWidth, Minecraft.getMinecraft.displayHeight
  ) {
  val entries: mutable.Buffer[GuiLayerEntry] =
    DimensionLayersPreset(settings).layers
      .map {
        case layer: DimensionLayerSpec => new GuiDimensionLayerEntry(this, layer)
        case layer: SolidLayerSpec => new GuiSolidLayerEntry(this, layer)
      }
      .toBuffer

  def add(block: IBlockState, height: Int): Unit = {
    new GuiSolidLayerEntry(this, SolidLayerSpec(block, height)) +=: entries
  }

  def add(dimensionType: DimensionType): Unit = {
    new GuiDimensionLayerEntry(this, DimensionLayerSpec(dimensionType)) +=: entries
  }

  def scrollUpOnce(): Unit = {
    val accessor = this.asInstanceOf[AccessorGuiScrollingList]
    accessor.setScrollDistance(accessor.getScrollDistance - this.slotHeight)
    accessor.invokeApplyScrollLimits()
  }

  def scrollDownOnce(): Unit = {
    val accessor = this.asInstanceOf[AccessorGuiScrollingList]
    accessor.setScrollDistance(accessor.getScrollDistance + this.slotHeight)
    accessor.invokeApplyScrollLimits()
  }

  def toSettings: String = DimensionLayersPreset(entries.map(_.layer).toList).toSettings

  override def getSize: Int = entries.size

  def entryHeight = slotHeight

  override def drawSlot(index: Int, right: Int, top: Int, height: Int, tess: Tessellator): Unit = {
    if (entries.indices contains index)
      entries(index).drawEntry(index, left, top, right, top + height, mouseX, mouseY)
  }

  override def elementClicked(index: Int, doubleClick: Boolean): Unit = {
    if (entries.indices contains index)
      entries(index).clicked(index, mouseX, mouseY)
  }

  override def isSelected(index: Int): Boolean = false

  override def drawBackground(): Unit = ()
}
