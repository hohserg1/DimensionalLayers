package hohserg.dimensional.layers.gui.settings.base

import hohserg.dimensional.layers.gui.GuiBase
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer.{gridCellSize, texture}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

package object offsets {

  def addOffsetsWidgets(topOffset: ValueHolder[Int], bottomOffset: ValueHolder[Int], left: Int, right: Int)(implicit gui: GuiBase): Unit = {
    val gridLeft = math.max(left, (right + left) / 2 - 14 / 2)
    val gridTop = gui.height / 2 - 209 / 2
    val topOffsetField = gui.addElement(new GuiOffsetField(gridLeft, gridTop, topOffset, null))
    val bottomOffsetField = gui.addElement(new GuiOffsetField(gridLeft, gridTop, bottomOffset, topOffsetField))
    gui.addFreeDrawable(() => drawLayerGrid(topOffset, bottomOffset, gui, gridLeft, gridTop))
  }


  def drawLayerGrid(topOffset: ValueHolder[Int], bottomOffset: ValueHolder[Int], gui: GuiBase, gridLeft: Int, gridTop: Int): Unit = {
    GlStateManager.color(1, 1, 1, 1)
    GlStateManager.disableLighting()
    val firstEnabled = 0 + topOffset.get
    val lastEnabled = 15 - bottomOffset.get

    Minecraft.getMinecraft.getTextureManager.bindTexture(texture)

    gui.drawTexturedModalRect(gridLeft, gridTop, 0, 0, 14, 209)

    for {
      i <- 0 until firstEnabled
    } drawDisabledCell(i, gui, gridLeft, gridTop)

    for {
      i <- firstEnabled to lastEnabled
    } drawEnabledCell(i, gui, gridLeft, gridTop)

    for {
      i <- 15 until lastEnabled by -1
    } drawDisabledCell(i, gui, gridLeft, gridTop)

  }

  def drawDisabledCell(i: Int, gui: GuiBase, gridLeft: Int, gridTop: Int): Unit = {
    gui.drawTexturedModalRect(gridLeft + 1, gridTop + i * gridCellSize + 1, 15, 14, 12, 12)
  }

  def drawEnabledCell(i: Int, gui: GuiBase, gridLeft: Int, gridTop: Int): Unit = {
    gui.drawTexturedModalRect(gridLeft + 1, gridTop + i * gridCellSize + 1, 15, 1, 12, 12)
  }

}
