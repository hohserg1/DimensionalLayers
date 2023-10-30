package hohserg.dimensional.layers.gui

import com.google.common.base.Predicate
import hohserg.dimensional.layers.gui.GuiNumericField.NumberHolder
import net.minecraft.client.gui.{FontRenderer, GuiTextField}

import scala.util.Try

object GuiNumericField {
  abstract class NumberHolder[N: Numeric](private var value: N) {
    def get: N = value

    def set(v: N): Unit = value = validate(v)

    def validate(v: N): N
  }
}

class GuiNumericField[N: Numeric](id: Int, x: Int, y: Int, maxLen: Int, value: NumberHolder[N], fromString: String => N, h: Int = 18)
                                 (implicit fontRenderer: FontRenderer)
  extends GuiTextField(id, fontRenderer, x, y, maxLen * fontRenderer.getCharWidth('0') + 8, h) {

  setText(value.get.toString)

  // Try(fromString(str)).map(clamp(_, minValue, maxValue)).getOrElse(default)

  setMaxStringLength(maxLen)

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
