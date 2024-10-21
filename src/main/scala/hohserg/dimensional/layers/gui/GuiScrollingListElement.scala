package hohserg.dimensional.layers.gui

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.client.GuiScrollingList
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
abstract class GuiScrollingListElement(x: Int, y: Int, w: Int, h: Int, entryHeight: Int)
  extends GuiScrollingList(
    Minecraft.getMinecraft, w, h, y, y + h, x, entryHeight,
    Minecraft.getMinecraft.displayWidth, Minecraft.getMinecraft.displayHeight
  )
    with GuiElement {

  def setScrollDistanceWithLimits(v: Float): Unit = {
    AccessorGuiScrollingList.scrollDistance.set(this, v)
    AccessorGuiScrollingList.applyScrollLimits(this)
  }

  def scrollToElement(index: Int): Unit = {
    setScrollDistanceWithLimits((slotHeight * index - listHeight.toDouble / 2 + slotHeight.toDouble / 2).toInt)
  }

  override def draw: Option[(Int, Int, Float) => Unit] = Some(drawScreen)

  override def mouseInput: Option[(Int, Int) => Unit] = Some(handleMouseInput)
}
