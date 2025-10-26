package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.gui.GuiTileList.SelectHandler
import hohserg.dimensional.layers.gui.settings.solid.GuiBlocksList
import net.minecraft.block.Block

class GuiBlockSelector(parent: GuiBase & SelectHandler[GuiBlocksList.DrawableBlock], selected: Option[Block]) extends GuiBase(parent) with SelectHandler[GuiBlocksList.DrawableBlock] {

  override def initGui(): Unit = {
    super.initGui()

    addButton(new GuiClickableButton(width - 80 - 10, height - 30, 80, 20, "Cancel")(back))

    val list = addElement(new GuiBlocksList(this, 10, 10, width - 210, height - 20))
    selected.foreach(b => list.select(GuiBlocksList.DrawableBlock(b)))
  }

  override def onSelected(item: GuiBlocksList.DrawableBlock): Unit = {
    parent.onSelected(item)
    back()
  }
}
