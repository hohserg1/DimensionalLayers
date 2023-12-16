package hohserg.dimensional.layers.gui

import com.google.common.base.Predicate
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import net.minecraft.client.gui.GuiTextField

import scala.util.Try

class GuiTextFieldElement[A](x: Int, y: Int, w: Int, h: Int, value: ValueHolder[A], fromString: String => A)
                            (implicit gui: GuiBase)
  extends GuiTextField(gui.nextElementId(), gui.fr, x, y, w, h) {

  setText(value.get.toString)


  setValidator(new Predicate[String] {
    override def apply(input: String): Boolean = input.isEmpty || Try(fromString(input)).isSuccess
  })

  override def setText(textIn: String): Unit = {
    Try(fromString(textIn)).foreach { nv =>
      value.set(nv)
      super.setText(value.get.toString)
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
}
