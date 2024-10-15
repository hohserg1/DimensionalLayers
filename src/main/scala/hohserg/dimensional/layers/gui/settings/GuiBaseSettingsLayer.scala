package hohserg.dimensional.layers.gui.settings

import hohserg.dimensional.layers.gui.GuiBaseSettings
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.preset.LayerSpec
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.util.Try

@SideOnly(Side.CLIENT)
abstract class GuiBaseSettingsLayer(parent: GuiSetupDimensionalLayersPreset, index: Int) extends GuiBaseSettings(parent) {

  def buildLayerSpec(): LayerSpec

  override def done(): Unit = {
    parent.layersList.entries.update(index, buildLayerSpec().toGuiLayerEntry(parent.layersList))
    super.done()
  }

  def toLongSeed(str: String): Option[Long] = {
    if (str.isEmpty)
      None
    else
      Some(Try(str.toLong)
        .filter(_ != 0)
        .getOrElse(str.hashCode.toLong))
  }
}
