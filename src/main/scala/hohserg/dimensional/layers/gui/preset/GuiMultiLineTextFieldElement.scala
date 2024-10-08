package hohserg.dimensional.layers.gui.preset

import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.{GuiBase, GuiEditableElement}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class GuiMultiLineTextFieldElement(x: Int, y: Int, w: Int, h: Int, value: ValueHolder[String])(implicit gui: GuiBase)
  extends GuiMultiLineTextField(gui.nextElementId(), gui.fr, x, y, w, h)
    with GuiEditableElement[String] {

  setMaxStringLength(value.get.length)

  value.initControlElement(this)

  override def updateVisual(v: String): Unit =
    super.setText(v)

  override def setText(textIn: String): Unit = {
    value.set(textIn)
    updateVisual(value.get)
  }

  override def writeText(textToWrite: String): Unit = {
    super.writeText(textToWrite)
    setText(getText)
  }

  override def deleteFromCursor(num: Int): Unit = {
    super.deleteFromCursor(num)
    setText(getText)
  }

  override def draw: Option[(Int, Int, Float) => Unit] = Some((_, _, _) => drawTextBox())

  override def mouseClick: Option[(Int, Int, Int) => Unit] = Some(mouseClicked)

  override def keyTyped: Option[(Char, Int) => Unit] = Some(textboxKeyTyped)
}
