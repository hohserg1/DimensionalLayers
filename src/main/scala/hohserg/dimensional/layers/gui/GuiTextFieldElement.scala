package hohserg.dimensional.layers.gui

import net.minecraft.client.gui.{FontRenderer, GuiTextField}

class GuiTextFieldElement(id: Int, x: Int, y: Int, w: Int, h: Int, val originalValue: String)(implicit fontRenderer: FontRenderer)
  extends GuiTextField(id, fontRenderer, x, y, w, h)
    with GuiElementControl[String] {

  setMaxStringLength(originalValue.length)
  setText(originalValue)

  override def draw: Option[(Int, Int, Float) => Unit] = Some((_, _, _) => drawTextBox())

  override def mouseClick: Option[(Int, Int, Int) => Unit] = Some(mouseClicked)

  override def keyTyped: Option[(Char, Int) => Unit] = Some(textboxKeyTyped)

  override def currentValue: String = getText
}
