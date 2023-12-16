package hohserg.dimensional.layers.gui.add.solid

import hohserg.dimensional.layers.DimensionalLayersPreset.SolidLayerSpec
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.solid
import net.minecraft.init.Blocks

class GuiAddLayer(parent: GuiSetupDimensionalLayersPreset) extends solid.GuiSettingsLayer(parent, SolidLayerSpec(Blocks.AIR.getDefaultState), 0) {
  override def done(): Unit = {
    parent.layersList.add(buildLayerSpec())
    back()
  }
}
