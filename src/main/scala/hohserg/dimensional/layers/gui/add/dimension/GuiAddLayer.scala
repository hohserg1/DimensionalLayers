package hohserg.dimensional.layers.gui.add.dimension

import hohserg.dimensional.layers.gui.add.dimension.GuiDimensionList.DrawableDim
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.{GuiBase, GuiClickableButton, GuiTileList}
import hohserg.dimensional.layers.preset.DimensionLayerSpec

class GuiAddLayer(parent: GuiSetupDimensionalLayersPreset) extends GuiBase(parent) with GuiTileList.SelectHandler[DrawableDim] {
  override def initGui(): Unit = {
    super.initGui()
    addButton(new GuiClickableButton(width - 100, height - 30, 90, 20, "Cancel")(back))
    addElement(new GuiDimensionList(this, width - 110))
  }

  override def onSelected(item: DrawableDim): Unit = {
    parent.layersList.add(DimensionLayerSpec(item.dimensionType))
    back()
  }
}
