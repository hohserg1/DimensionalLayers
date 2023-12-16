package hohserg.dimensional.layers.gui.preset

import hohserg.dimensional.layers.gui.add._
import hohserg.dimensional.layers.gui.preset.list.GuiLayersList
import hohserg.dimensional.layers.gui.{GuiBase, GuiClickableButton}
import net.minecraft.client.gui.GuiCreateWorld

import java.util.Random

class GuiSetupDimensionalLayersPreset(parent: GuiCreateWorld) extends GuiBase(parent) {
  var layersList: GuiLayersList = _
  var exportButton: GuiClickableButton = _

  override protected def back(): Unit = {
    if (parent.worldSeed.isEmpty)
      parent.worldSeed = new Random().nextLong.toString
    super.back()
  }

  override def initGui(): Unit = {
    super.initGui()

    addButton(new GuiClickableButton(width - 80 - 10, height - 30, 80, 20, "Done")(() => {
      parent.chunkProviderSettingsJson = layersList.toSettings
      back()
    }))

    addButton(new GuiClickableButton(width - 80 - 10 - 80 - 10, height - 30, 80, 20, "Cancel")(back))

    addButton(new GuiClickableButton(width - 110 - 10, 10, 110, 20, "Add dimension layer")(show(new dimension.GuiAddLayer(_))))

    addButton(new GuiClickableButton(width - 110 - 10, 30, 110, 20, "Add solid layer")(show(new solid.GuiAddLayer(_))))

    addButton(new GuiClickableButton(width - 110 - 10, height - 30 - 20 - 10, 110, 20, "Import preset")(show(new GuiImportPreset(_))))
    exportButton = addButton(new GuiClickableButton(width - 110 - 10, height - 30 - 20 - 1 - 20 - 10, 110, 20, "Export preset")(GuiImportPreset.export(this)))

    initFromJson(if (layersList == null) parent.chunkProviderSettingsJson else layersList.toSettings)
  }

  def initFromJson(preset: String): Unit = {
    layersList = addElement(new GuiLayersList(this, preset, if (layersList == null) 0 else layersList.accessor.getScrollDistance))
  }
}
