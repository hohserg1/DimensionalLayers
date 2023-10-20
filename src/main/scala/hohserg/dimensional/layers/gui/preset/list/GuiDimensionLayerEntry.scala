package hohserg.dimensional.layers.gui.preset.list

import hohserg.dimensional.layers.DimensionLayersPreset.DimensionLayerSpec
import hohserg.dimensional.layers.gui.DimensionClientUtils

class GuiDimensionLayerEntry(val parent: GuiLayersList, val layer: DimensionLayerSpec) extends GuiLayerEntry {
  override def drawEntry(index: Int, minX: Int, minY: Int, maxX: Int, maxY: Int, mouseX: Int, mouseY: Int): Unit = {
    super.drawEntry(index, minX, minY, maxX, maxY, mouseX, mouseY)
    DimensionClientUtils.drawLogo(layer.dimensionType, minX, minY)
    mc.fontRenderer.drawStringWithShadow(DimensionClientUtils.getDisplayName(layer.dimensionType), minX + DimensionClientUtils.width + 4, minY + (maxY - minY) / 2 - 5, 0xffffff)
  }
}
