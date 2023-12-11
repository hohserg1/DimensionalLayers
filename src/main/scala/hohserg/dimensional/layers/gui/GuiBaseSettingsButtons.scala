package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset

class GuiBaseSettingsButtons(parent: GuiSetupDimensionalLayersPreset) extends GuiBase(parent) {

  def done(): Unit = {
    back()
  }

  def markChanged(): Unit = {
    hasChanges = true
    doneButton.enabled = true
  }

  override def addElement[E <: GuiElement](e: E): E = {
    super.addElement(e)
    e match {
      case control: GuiElementControl[_] =>
        control.changeHandler = markChanged
      case _ =>
    }
    e
  }

  var hasChanges = false

  var doneButton: GuiClickableButton = _

  override def initGui(): Unit = {
    super.initGui()

    addButton(new GuiClickableButton(0, width - 80 - 10, height - 30, 80, 20, "Cancel")(back))

    doneButton = addButton(new GuiClickableButton(1, width - 80 - 10, 10, 80, 20, "Done")(done) {
      enabled = hasChanges
    })
  }
}
