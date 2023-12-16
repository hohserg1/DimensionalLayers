package hohserg.dimensional.layers.gui.settings

import hohserg.dimensional.layers.DimensionalLayersPreset.LayerSpec
import hohserg.dimensional.layers.gui.GuiBaseSettings
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.preset.list.GuiLayerEntry

abstract class GuiBaseSettingsLayer(parent: GuiSetupDimensionalLayersPreset, index: Int) extends GuiBaseSettings(parent) {

  def buildLayerSpec(): LayerSpec

  override def done(): Unit = {
    parent.layersList.entries.update(index, GuiLayerEntry(parent.layersList, buildLayerSpec()))
    super.done()
  }
}
