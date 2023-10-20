package hohserg.dimensional.layers.gui.preset

import hohserg.dimensional.layers.gui.add._
import hohserg.dimensional.layers.gui.preset.list.GuiLayersList
import hohserg.dimensional.layers.gui.{GuiBase, GuiClickableButton, MouseUtils}
import net.minecraft.client.gui.GuiCreateWorld

class GuiSetupDimensionLayersPreset(parent: GuiCreateWorld) extends GuiBase(parent) {
  var layersList: GuiLayersList = _

  override def initGui(): Unit = {
    super.initGui()

    addButton(new GuiClickableButton(0, width - 80 - 10, height - 30, 80, 20, "Done")(() => {
      parent.chunkProviderSettingsJson = layersList.toSettings
      back()
    }))

    addButton(new GuiClickableButton(1, width - 80 - 10 - 80 - 10, height - 30, 80, 20, "Cancel")(back))

    addButton(new GuiClickableButton(2, width - 110 - 10, 10, 110, 20, "Add dimension layer")(show(new dimension.GuiAddLayer(_))))

    addButton(new GuiClickableButton(3, width - 110 - 10, 30, 110, 20, "Add solid layer")(show(new solid.GuiAddLayer(_))))

    layersList = new GuiLayersList(this, width - 200, height, if (layersList == null) parent.chunkProviderSettingsJson else layersList.toSettings)
  }

  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    drawDefaultBackground()
    super.drawScreen(mouseX, mouseY, partialTicks)
    layersList.drawScreen(mouseX, mouseY, partialTicks)
  }

  override def handleMouseInput(): Unit = {
    super.handleMouseInput()
    val (mouseX, mouseY) = MouseUtils.getMousePos(parent)
    layersList.handleMouseInput(mouseX, mouseY)
  }
}
