package hohserg.dimensional.layers.gui.preset

import hohserg.dimensional.layers.gui.GuiElementControl
import net.minecraft.client.gui.FontRenderer

class GuiMultiLineTextFieldElement(id: Int, x: Int, y: Int, w: Int, h: Int, init: String)(implicit fontRenderer: FontRenderer)
  extends GuiMultiLineTextField(id, fontRenderer, x, y, w, h)
    with GuiElementControl[String] {
  override def draw: Option[(Int, Int, Float) => Unit] = Some((_, _, _) => drawTextBox())

  override def mouseClick: Option[(Int, Int, Int) => Unit] = Some(mouseClicked)

  override def keyTyped: Option[(Char, Int) => Unit] = Some(textboxKeyTyped)

  override val originalValue: String = init

  override def currentValue: String = getText
}
