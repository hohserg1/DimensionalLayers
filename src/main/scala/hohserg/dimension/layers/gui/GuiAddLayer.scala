package hohserg.dimension.layers.gui

import net.minecraft.client.gui.{GuiButton, GuiScreen}

class GuiAddLayer(parent: GuiSetupDimensionLayersPreset) extends GuiScreen {
  override def initGui(): Unit = {
    super.initGui()
    buttonList.add(new GuiButton(0, width - 100, 10, 90, 20, "Back"))
  }

  override def actionPerformed(button: GuiButton): Unit = {
    mc.displayGuiScreen(parent)
  }


  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    drawDefaultBackground()
    super.drawScreen(mouseX, mouseY, partialTicks)
  }

}
