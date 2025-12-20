package hohserg.dimensional.layers.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
trait GuiElement {
  def draw: Option[(Int, Int, Float) => Unit] = None

  def mouseInput: Option[(Int, Int) => Unit] = None

  def mouseClick: Option[(Int, Int, Int) => Unit] = None

  def mouseClickMove: Option[(Int, Int, Int) => Unit] = None

  def mouseRelease: Option[(Int, Int, Int) => Unit] = None

  def keyTyped: Option[(Char, Int) => Unit] = None
  
  protected lazy val drawHoveringText: (String, Int, Int) => Unit = {
    val g = new GuiScreen {}
    g.setWorldAndResolution(Minecraft.getMinecraft, Minecraft.getMinecraft.displayWidth, Minecraft.getMinecraft.displayHeight)
    g.drawHoveringText
  }

}
