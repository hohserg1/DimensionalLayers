package hohserg.dimensional.layers.gui.add.dimension

import hohserg.dimensional.layers.gui.GuiSelectDimension
import hohserg.dimensional.layers.gui.GuiSelectDimension.DrawableDim
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.preset.spec.DimensionLayerSpec
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class GuiAddLayer(parent: GuiSetupDimensionalLayersPreset) extends GuiSelectDimension(parent) {
  override def onSelected(item: DrawableDim): Unit = {
    parent.layersList.add(DimensionLayerSpec(item.dimensionType))
    back()
  }
}
