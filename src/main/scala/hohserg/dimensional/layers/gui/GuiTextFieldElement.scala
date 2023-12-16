package hohserg.dimensional.layers.gui

import com.google.common.base.Predicate
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import net.minecraft.client.gui.GuiTextField

import scala.util.Try

class GuiTextFieldElement[A](x: Int, y: Int, w: Int, h: Int, value: ValueHolder[A], fromString: String => A)
                            (implicit gui: GuiBase)
  extends GuiTextField(gui.nextElementId(), gui.fr, x, y, w, h)
    with GuiEditableElement[A] {

  value.initControlElement(this)

  setValidator(new Predicate[String] {
    override def apply(input: String): Boolean = input.isEmpty || Try(fromString(input)).isSuccess
  })

  override def updateVisual(v: A): Unit =
    super.setText(v.toString)

  override def setText(textIn: String): Unit = {
    Try(fromString(textIn)).foreach { nv =>
      value.set(nv)
      updateVisual(value.get)
    }
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
