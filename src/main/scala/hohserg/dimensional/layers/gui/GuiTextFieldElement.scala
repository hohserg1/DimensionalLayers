package hohserg.dimensional.layers.gui

import com.google.common.base.Predicate
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import net.minecraft.client.gui.GuiTextField
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.util.Try

@SideOnly(Side.CLIENT)
class GuiTextFieldElement[A](x: Int, y: Int, w: Int, h: Int, value: ValueHolder[A], fromString: String => A)
                            (implicit gui: GuiBase)
  extends GuiTextField(gui.nextElementId(), gui.fr, x, y, w, h)
    with GuiEditableElement[A] {

  var enabled = true

  value.initControlElement(this)

  setValidator((input: String) => input.isEmpty || Try(fromString(input)).isSuccess)

  override def updateVisual(v: A): Unit = {
    super.setText(v.toString)
  }

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

  var absMouseX: Int = 0
  var absMouseY: Int = 0

  override def draw: Option[(Int, Int, Float) => Unit] = {
    Some((mouseX, mouseY, _) => {
      if (enabled) {
        absMouseX = mouseX
        absMouseY = mouseY
        drawTextBox()
      }
    })
  }

  override def mouseClick: Option[(Int, Int, Int) => Unit] = Some(mouseClicked)

  override def keyTyped: Option[(Char, Int) => Unit] = Some(textboxKeyTyped)

  override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean = {
    if (enabled)
      super.mouseClicked(mouseX, mouseY, mouseButton)
    else
      false
  }

  override def textboxKeyTyped(typedChar: Char, keyCode: Int): Boolean = {
    if (enabled)
      super.textboxKeyTyped(typedChar, keyCode)
    else
      false
  }
}
