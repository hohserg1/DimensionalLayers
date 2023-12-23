package hohserg.dimensional.layers.gui.preset.list

import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.{DimensionClientUtils, GuiScrollingListElement}
import hohserg.dimensional.layers.preset._
import net.minecraft.client.renderer.Tessellator

import scala.collection.mutable

class GuiLayersList(val parent: GuiSetupDimensionalLayersPreset, settings: String, scrollDistance: Float)
  extends GuiScrollingListElement(10, 10, parent.width - 200, parent.height - 20, DimensionClientUtils.width + 4) {

  val entries: mutable.Buffer[GuiLayerEntry] =
    DimensionalLayersPreset(settings).layers
      .map {
        case layer: DimensionLayerSpec => new GuiDimensionLayerEntry(this, layer)
        case layer: SolidLayerSpec => new GuiSolidLayerEntry(this, layer)
      }
      .toBuffer

  setScrollDistanceWithLimits(scrollDistance)

  def add(layer: LayerSpec): Unit = {
    GuiLayerEntry(this, layer) +=: entries
  }

  def scrollUpOnce(): Unit = {
    setScrollDistanceWithLimits(accessor.getScrollDistance - this.slotHeight)
  }

  def scrollDownOnce(): Unit = {
    setScrollDistanceWithLimits(accessor.getScrollDistance + this.slotHeight)
  }

  def toSettings: String = DimensionalLayersPreset(entries.map(_.layer).toList).toSettings

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
