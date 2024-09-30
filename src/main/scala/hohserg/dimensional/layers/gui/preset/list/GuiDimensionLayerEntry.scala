package hohserg.dimensional.layers.gui.preset.list

import hohserg.dimensional.layers.gui.IconUtils._
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.dimension
import hohserg.dimensional.layers.gui.{GuiBase, makeDimensionTypeLabel}
import hohserg.dimensional.layers.preset.DimensionLayerSpec

class GuiDimensionLayerEntry(val parent: GuiLayersList, val layer: DimensionLayerSpec) extends GuiLayerEntry {
  override def drawEntry(index: Int, minX: Int, minY: Int, maxX: Int, maxY: Int, mouseX: Int, mouseY: Int): Unit = {
    super.drawEntry(index, minX, minY, maxX, maxY, mouseX, mouseY)
    drawLogo(layer.dimensionType, minX, minY)
    mc.fontRenderer.drawStringWithShadow(makeDimensionTypeLabel(layer.dimensionType), minX + width + 11, minY + (maxY - minY) / 2 - 5, 0xffffff)
  }

  override def guiSettings(index: Int, prevGui: GuiSetupDimensionalLayersPreset): GuiBase = new dimension.GuiSettingsLayer(prevGui, index, layer)
}
