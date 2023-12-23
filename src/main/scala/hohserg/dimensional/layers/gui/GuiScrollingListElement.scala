package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.gui.mixin.AccessorGuiScrollingList
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.client.GuiScrollingList

abstract class GuiScrollingListElement(x: Int, y: Int, w: Int, h: Int, entryHeight: Int)
  extends GuiScrollingList(
    Minecraft.getMinecraft, w, h, y, y + h, x, entryHeight,
    Minecraft.getMinecraft.displayWidth, Minecraft.getMinecraft.displayHeight
  )
    with GuiElement {

  val accessor = this.asInstanceOf[AccessorGuiScrollingList]

  def setScrollDistanceWithLimits(v: Float): Unit = {
    accessor.setScrollDistance(v)
    accessor.invokeApplyScrollLimits()
  }

  def scrollToElement(index: Int): Unit = {
    setScrollDistanceWithLimits((slotHeight * index - listHeight.toDouble / 2 + slotHeight.toDouble / 2).toInt)
  }

  override def draw: Option[(Int, Int, Float) => Unit] = Some(drawScreen)

  override def mouseInput: Option[(Int, Int) => Unit] = Some(handleMouseInput)
}
