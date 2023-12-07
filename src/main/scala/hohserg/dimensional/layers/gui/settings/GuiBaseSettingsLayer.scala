package hohserg.dimensional.layers.gui.settings

import hohserg.dimensional.layers.DimensionalLayersPreset.LayerSpec
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.preset.list.GuiLayerEntry
import hohserg.dimensional.layers.gui.{GuiBase, GuiClickableButton}

abstract class GuiBaseSettingsLayer(parent: GuiSetupDimensionalLayersPreset, layer: LayerSpec, index: Int) extends GuiBase(parent) {

  def buildLayerSpec(): LayerSpec

  def done(): Unit = {
    parent.layersList.entries.update(index, GuiLayerEntry(parent.layersList, buildLayerSpec()))
    back()
  }

  def markChanged(): Unit = {
    hasChanges = true
    doneButton.enabled = true
  }

  var hasChanges = false

  var doneButton: GuiClickableButton = _

  override def initGui(): Unit = {
    super.initGui()

    addButton(new GuiClickableButton(0, width - 100, height - 30, 90, 20, "Cancel")(back))

    doneButton = addButton(new GuiClickableButton(1, width - 100, 10, 90, 20, "Done")(done) {
      enabled = hasChanges
    })
  }
}
