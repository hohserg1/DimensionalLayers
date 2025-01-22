package hohserg.dimensional.layers.gui.settings

import hohserg.dimensional.layers.Main
import hohserg.dimensional.layers.gui.GuiBaseSettings
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.preset.spec.LayerSpec
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
abstract class GuiBaseSettingsLayer(parent: GuiSetupDimensionalLayersPreset, index: Int) extends GuiBaseSettings(parent) {

  def buildLayerSpec(): LayerSpec

  override def done(): Unit = {
    parent.layersList.entries.update(index, buildLayerSpec().toGuiLayerEntry(parent.layersList))
    super.done()
  }
}

object GuiBaseSettingsLayer {
  val texture = new ResourceLocation(Main.modid, "textures/gui/dimension_settings.png")
}
