package hohserg.dimension.layers.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Mouse

object MouseUtils {
  def getMousePos(gui: GuiScreen): (Int, Int) =
    (Mouse.getEventX * gui.width / Minecraft.getMinecraft.displayWidth, gui.height - Mouse.getEventY * gui.height / Minecraft.getMinecraft.displayHeight - 1)


}
