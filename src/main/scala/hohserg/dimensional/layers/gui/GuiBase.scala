package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.gui.GuiClickableButton.Handler
import net.minecraft.client.gui.{FontRenderer, GuiScreen}

class GuiBase(val parent: GuiScreen) extends GuiScreen {
  def fr: FontRenderer = fontRenderer

  implicit def self: this.type = this

  private var elementId = 0

  def nextElementId(): Int = {
    elementId += 1
    elementId
  }

  override def initGui(): Unit = {
    super.initGui()
    elementId = 0
  }

  protected def back(): Unit = {
    mc.displayGuiScreen(parent)
  }

  protected def show(nextGuiByParent: this.type => GuiScreen): Handler =
    () => mc.displayGuiScreen(nextGuiByParent(this))

  override def keyTyped(typedChar: Char, keyCode: Int): Unit = {
    if (keyCode == 1) {
      back()
      if (this.mc.currentScreen == null)
        this.mc.setIngameFocus()
    }
  }
}
