package hohserg.dimensional.layers.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Mouse

object MouseUtils {
  def getMousePos: (Int, Int) = {
    val resolution = new ScaledResolution(Minecraft.getMinecraft)
    val width = resolution.getScaledWidth
    val height = resolution.getScaledHeight
    (Mouse.getEventX * width / Minecraft.getMinecraft.displayWidth, height - Mouse.getEventY * height / Minecraft.getMinecraft.displayHeight - 1)
  }
}
