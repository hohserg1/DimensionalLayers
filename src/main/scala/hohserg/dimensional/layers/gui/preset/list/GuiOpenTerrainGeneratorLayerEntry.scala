package hohserg.dimensional.layers.gui.preset.list

import hohserg.dimensional.layers.gui.GuiBase
import hohserg.dimensional.layers.gui.IconUtils._
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.preset.OpenTerrainGeneratorLayerSpec

class GuiOpenTerrainGeneratorLayerEntry(val parent: GuiLayersList, val layer: OpenTerrainGeneratorLayerSpec) extends GuiLayerEntry {
  override def drawEntry(index: Int, minX: Int, minY: Int, maxX: Int, maxY: Int, mouseX: Int, mouseY: Int): Unit = {
    super.drawEntry(index, minX, minY, maxX, maxY, mouseX, mouseY)
    drawLogo(layer, minX, minY)

    mc.fontRenderer.drawStringWithShadow(layer.presetName, minX + width + 11, minY + (maxY - minY) / 2 + 4, 0xffffff)
  }

  override def guiSettings(index: Int, parent: GuiSetupDimensionalLayersPreset): GuiBase = ???
}
