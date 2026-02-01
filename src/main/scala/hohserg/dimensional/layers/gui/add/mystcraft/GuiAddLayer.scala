package hohserg.dimensional.layers.gui.add.mystcraft

import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.mystcraft
import hohserg.dimensional.layers.preset.spec.MystcraftLayerSpec
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class GuiAddLayer(parent: GuiSetupDimensionalLayersPreset) extends mystcraft.GuiSettingsLayer(parent, MystcraftLayerSpec(), 0) {
}
