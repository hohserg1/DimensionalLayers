package hohserg.dimension.layers.gui

import hohserg.dimension.layers._
import net.minecraft.client.gui.{GuiButton, GuiCreateWorld, GuiScreen}
import org.apache.commons.lang3.StringUtils

class GuiSetupDimensionLayersPreset(guiCreateWorld: GuiCreateWorld) extends GuiScreen {
  var currentPreset = DimensionLayersPreset.fromJson(
    Option(guiCreateWorld.chunkProviderSettingsJson)
      .filter(StringUtils.isNotEmpty)
      .getOrElse(Configuration.defaultPreset)
  )

  var layersList: GuiLayersList = _

  val doneButton = 0
  val addLayerButton = 1

  override def initGui(): Unit = {
    super.initGui()
    buttonList.add(new GuiButton(addLayerButton, width - 100, 10, 90, 20, "Add layer"))
    buttonList.add(new GuiButton(doneButton, width - 100, height - 30, 90, 20, "Done"))
    layersList = new GuiLayersList(this)
  }

  override def actionPerformed(button: GuiButton): Unit = {
    if (button.id == doneButton) {
      guiCreateWorld.chunkProviderSettingsJson = currentPreset.toJson
      mc.displayGuiScreen(guiCreateWorld)
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
    val (mouseX, mouseY) = MouseUtils.getMousePos(this)
    layersList.handleMouseInput(mouseX, mouseY)
  }
}
