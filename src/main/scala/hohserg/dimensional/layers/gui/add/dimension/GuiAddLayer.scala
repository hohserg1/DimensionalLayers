package hohserg.dimensional.layers.gui.add.dimension

import hohserg.dimensional.layers.gui.add.dimension.GuiDimensionList.DrawableDim
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.{GuiBase, GuiClickableButton, GuiTileList, MouseUtils}

class GuiAddLayer(parent: GuiSetupDimensionalLayersPreset) extends GuiBase(parent) with GuiTileList.SelectHandler[DrawableDim] {
  var dimensionTypesList: GuiDimensionList = _

  override def initGui(): Unit = {
    super.initGui()
    addButton(new GuiClickableButton(width - 100, height - 30, 90, 20, "Cancel")(back))
    dimensionTypesList = new GuiDimensionList(this, width - 110, height)
  }

  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    drawDefaultBackground()
    super.drawScreen(mouseX, mouseY, partialTicks)
    dimensionTypesList.drawScreen(mouseX, mouseY, partialTicks)
  }

  override def handleMouseInput(): Unit = {
    super.handleMouseInput()
    val (mouseX, mouseY) = MouseUtils.getMousePos
    dimensionTypesList.handleMouseInput(mouseX, mouseY)
  }

  override def onSelected(item: DrawableDim): Unit = {
    parent.layersList.add(item.dimensionType)
    back()
  }
}
