package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.clamp
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.client.GuiScrollingList
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.input.Mouse

@SideOnly(Side.CLIENT)
abstract class GuiScrollingListElement(val x: Int, val y: Int, val w: Int, val h: Int, entryHeight: Int)
  extends GuiScrollingList(
    Minecraft.getMinecraft, w, h, y, y + h, x, entryHeight,
    Minecraft.getMinecraft.displayWidth, Minecraft.getMinecraft.displayHeight
  )
    with GuiElement {

  var enabled = true

  def setScrollDistanceWithLimits(v: Float): Unit = {
    GuiScrollingListLens.scrollDistance.set(this, v)
    GuiScrollingListLens.applyScrollLimits(this)
  }

  def scrollToElement(index: Int): Unit = {
    setScrollDistanceWithLimits((slotHeight * index - listHeight.toDouble / 2 + slotHeight.toDouble / 2).toInt)
  }

  override def draw: Option[(Int, Int, Float) => Unit] = Some(drawScreen)

  override def mouseInput: Option[(Int, Int) => Unit] = Some(handleMouseInput)

  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    if (enabled) {
      super.drawScreen(mouseX, mouseY, partialTicks)
      drawHoveringHighlight()
    }
  }


  def drawHoveringHighlight(): Unit = {
    val isHovering = mouseX >= this.left && mouseX <= this.left + this.listWidth && mouseY >= this.top && mouseY <= this.bottom
    if (isHovering) {
      val border = 4
      val mouseListY = mouseY - this.top - GuiScrollingListLens.headerHeight.get(this) + GuiScrollingListLens.scrollDistance.get(this).toInt - border
      val slotIndex = mouseListY / this.slotHeight
      val right = this.left + this.listWidth - 6 - 1
      if (mouseX >= left && mouseX <= right && slotIndex >= 0 && mouseListY >= 0 && slotIndex < getSize) {
        val baseY = top + border - GuiScrollingListLens.scrollDistance.get(this).toInt
        val slotTop = baseY + slotIndex * this.slotHeight + GuiScrollingListLens.headerHeight.get(this)
        val slotBuffer = this.slotHeight - border
        drawHighlightHovering(left, slotTop, right - left, slotBuffer)
      }
    }
  }


  override def handleMouseInput(mouseX: Int, mouseY: Int): Unit = {
    if (enabled) {
      val isHovering = mouseX >= left && mouseX <= left + listWidth && mouseY >= top && mouseY <= bottom
      if (isHovering) {
        val scroll = Mouse.getEventDWheel
        if (scroll != 0)
          setScrollDistanceWithLimits(GuiScrollingListLens.scrollDistance.get(this) +
            (-1 * clamp(scroll, -1, 1) * this.slotHeight / 2).toFloat
          )
      }
    }
  }
}
