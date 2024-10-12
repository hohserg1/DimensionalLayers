package hohserg.dimensional.layers.gui

import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
trait GuiEditableElement[A] extends GuiElement {

  def updateVisual(v: A): Unit

}
