package hohserg.dimensional.layers.preset.spec

import hohserg.dimensional.layers.data.layer.otg.OpenTerrainGeneratorLayer
import hohserg.dimensional.layers.gui.preset.list.{GuiLayerEntry, GuiLayersList, GuiOpenTerrainGeneratorLayerEntry}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

case class OpenTerrainGeneratorLayerSpec(presetName: String, seedOverride: Option[Long] = None) extends LayerSpec {
  override def height: Int = 16

  override val toLayer = OpenTerrainGeneratorLayer

  @SideOnly(Side.CLIENT)
  override def toGuiLayerEntry(parent: GuiLayersList): GuiLayerEntry = new GuiOpenTerrainGeneratorLayerEntry(parent, this)
}
