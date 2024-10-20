package hohserg.dimensional.layers.gui.add.solid

import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.solid
import hohserg.dimensional.layers.preset.spec.SolidLayerSpec
import net.minecraft.init.Blocks
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class GuiAddLayer(parent: GuiSetupDimensionalLayersPreset) extends solid.GuiSettingsLayer(parent, SolidLayerSpec(Blocks.AIR.getDefaultState, 1), 0) {
  override def done(): Unit = {
    parent.layersList.add(buildLayerSpec())
    back()
  }
}
