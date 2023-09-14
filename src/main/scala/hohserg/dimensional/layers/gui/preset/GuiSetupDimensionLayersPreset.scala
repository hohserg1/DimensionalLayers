package hohserg.dimensional.layers.gui.preset

import hohserg.dimensional.layers.gui.MouseUtils
import hohserg.dimensional.layers.gui.add.GuiAddLayer
import net.minecraft.client.gui.{GuiButton, GuiCreateWorld, GuiScreen}

class GuiSetupDimensionLayersPreset(parent: GuiCreateWorld) extends GuiScreen {
  var layersList: GuiLayersList = _

  val doneButton = 0
  val addLayerButton = 1

  override def initGui(): Unit = {
    super.initGui()
    addButton(new GuiButton(addLayerButton, width - 100, 10, 90, 20, "Add layer"))
    addButton(new GuiButton(doneButton, width - 100, height - 30, 90, 20, "Done"))
    layersList = new GuiLayersList(this, width - 200, height, parent.chunkProviderSettingsJson)
  }

  override def actionPerformed(button: GuiButton): Unit = {
    if (button.id == doneButton) {
      parent.chunkProviderSettingsJson = layersList.toSettings
      mc.displayGuiScreen(parent)
    } else if (button.id == addLayerButton) {
      mc.displayGuiScreen(new GuiAddLayer(this))
    }
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
