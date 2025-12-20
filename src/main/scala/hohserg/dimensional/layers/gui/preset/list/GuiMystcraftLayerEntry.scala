package hohserg.dimensional.layers.gui.preset.list

import hohserg.dimensional.layers.gui.GuiBase
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.mystcraft.GuiSettingsLayer
import hohserg.dimensional.layers.preset.spec.MystcraftLayerSpec
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class GuiMystcraftLayerEntry(val parent: GuiLayersList, val layer: MystcraftLayerSpec) extends GuiLayerEntry {

  override def guiSettings(index: Int, prevGui: GuiSetupDimensionalLayersPreset): GuiBase = new GuiSettingsLayer(prevGui, layer, index)
}
