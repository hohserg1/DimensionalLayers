package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import net.minecraft.client.gui.GuiScreen

import scala.collection.mutable.ListBuffer

class GuiBaseSettings(parent: GuiScreen) extends GuiBase(parent) {
  def done(): Unit = {
    back()
  }

  var doneButton: GuiClickableButton = _

  override def initGui(): Unit = {
    super.initGui()

    addButton(new GuiClickableButton(width - 80 - 10, height - 30, 80, 20, "Cancel")(back))

    doneButton = addButton(new GuiClickableButton(width - 80 - 10, 10, 80, 20, "Done")(done) {
      enabled = hasChanges
    })
  }

  private val state = new ListBuffer[ValueHolder[_]]

  def hasChanges: Boolean = state.exists(_.hasChanges)

  private def onStateChanged(): Unit = {
    doneButton.enabled = hasChanges
  }

}

object GuiBaseSettings {

  class ValueHolder[A](init: A, validate: A => A = (v: A) => v)(implicit gui: GuiBaseSettings) {
    private var value: A = init
    private[GuiBaseSettings] var hasChanges = false

    gui.state += this

    private var controlElement: GuiEditableElement[A] = _

    def initControlElement(e: GuiEditableElement[A]): Unit = {
      controlElement = e
      controlElement.updateVisual(get)
    }

    def get: A = value

    def set(v: A): Unit = {
      value = validate(v)
      hasChanges = value != init
      gui.onStateChanged()

      if (controlElement != null)
        controlElement.updateVisual(value)
    }
  }
}
