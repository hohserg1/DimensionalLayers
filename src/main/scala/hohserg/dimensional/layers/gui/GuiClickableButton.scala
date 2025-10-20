package hohserg.dimensional.layers.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

type Handler = () => Unit

@SideOnly(Side.CLIENT)
class GuiClickableButton(x: Int, y: Int, w: Int, h: Int, label: String)(clicked: Handler)(implicit gui: GuiBase) extends GuiButton(gui.nextElementId(), x, y, w, h, label) {
  override def mousePressed(mc: Minecraft, mouseX: Int, mouseY: Int): Boolean = {
    val r = super.mousePressed(mc, mouseX, mouseY)
    if (r)
      clicked()
    r
  }
}
