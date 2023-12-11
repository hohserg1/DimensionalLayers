package hohserg.dimensional.layers.gui.preset

import hohserg.dimensional.layers.gui.add._
import hohserg.dimensional.layers.gui.preset.list.GuiLayersList
import hohserg.dimensional.layers.gui.{GuiBase, GuiClickableButton, MouseUtils}
import net.minecraft.client.gui.GuiCreateWorld

import java.util.Random

class GuiSetupDimensionalLayersPreset(parent: GuiCreateWorld) extends GuiBase(parent) {
  var layersList: GuiLayersList = _

  override protected def back(): Unit = {
    if (parent.worldSeed.isEmpty)
      parent.worldSeed = new Random().nextLong.toString
    super.back()
  }

  override def initGui(): Unit = {
    super.initGui()

    addButton(new GuiClickableButton(0, width - 80 - 10, height - 30, 80, 20, "Done")(() => {
      parent.chunkProviderSettingsJson = layersList.toSettings
      back()
    }))

    addButton(new GuiClickableButton(1, width - 80 - 10 - 80 - 10, height - 30, 80, 20, "Cancel")(back))

    addButton(new GuiClickableButton(2, width - 110 - 10, 10, 110, 20, "Add dimension layer")(show(new dimension.GuiAddLayer(_))))

    addButton(new GuiClickableButton(3, width - 110 - 10, 30, 110, 20, "Add solid layer")(show(new solid.GuiAddLayer(_))))

    addButton(new GuiClickableButton(4, width - 110 - 10, height - 30, 110, 20, "Import preset")(show(new GuiImportPreset(_))))
    addButton(new GuiClickableButton(5, width - 110 - 10, height - 30 - 20 - 1, 110, 20, "Export preset")(GuiImportPreset.export(this)))

    initFromJson(if (layersList == null) parent.chunkProviderSettingsJson else layersList.toSettings)
  }

  def initFromJson(preset: String): Unit = {
    layersList = new GuiLayersList(this, width - 200, height, preset)
  }

  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    drawDefaultBackground()
    super.drawScreen(mouseX, mouseY, partialTicks)
    layersList.drawScreen(mouseX, mouseY, partialTicks)
  }

  override def handleMouseInput(): Unit = {
    super.handleMouseInput()
    val (mouseX, mouseY) = MouseUtils.getMousePos
    layersList.handleMouseInput(mouseX, mouseY)
  }
}
