package hohserg.dimensional.layers.gui.add

import hohserg.dimensional.layers.gui.MouseUtils
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionLayersPreset
import net.minecraft.client.gui.{GuiButton, GuiScreen}
import net.minecraft.world.DimensionType

class GuiAddLayer(parent: GuiSetupDimensionLayersPreset) extends GuiScreen {
  var dimensionTypesList: GuiDimensionList = _

  override def initGui(): Unit = {
    super.initGui()
    addButton(new GuiButton(0, width - 100, height - 30, 90, 20, "Cancel"))
    dimensionTypesList = new GuiDimensionList(this, width - 110, height)()
  }

  def select(dimensionType: DimensionType): Unit = {
    parent.layersList.add(dimensionType)
    back()
  }

  override def actionPerformed(button: GuiButton): Unit = {
    back()
  }

  override protected def keyTyped(typedChar: Char, keyCode: Int): Unit = {
    if (keyCode == 1)
      back()
    else
      super.keyTyped(typedChar, keyCode)
  }

  private def back(): Unit = {
    mc.displayGuiScreen(parent)
  }

  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    drawDefaultBackground()
    super.drawScreen(mouseX, mouseY, partialTicks)
    dimensionTypesList.drawScreen(mouseX, mouseY, partialTicks)
  }

  override def handleMouseInput(): Unit = {
    super.handleMouseInput()
    val (mouseX, mouseY) = MouseUtils.getMousePos(parent)
    dimensionTypesList.handleMouseInput(mouseX, mouseY)
  }
}
