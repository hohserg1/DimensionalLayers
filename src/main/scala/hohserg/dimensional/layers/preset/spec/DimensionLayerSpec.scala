package hohserg.dimensional.layers.preset.spec

import hohserg.dimensional.layers.data.layer.vanilla_dimension.VanillaDimensionLayer
import hohserg.dimensional.layers.gui.preset.list.{GuiDimensionLayerEntry, GuiLayerEntry, GuiLayersList}
import net.minecraft.world.{DimensionType, WorldType}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

case class DimensionLayerSpec(dimensionType: DimensionType,
                              seedOverride: Option[Long] = None,
                              topOffset: Int = 0, bottomOffset: Int = 0,
                              worldType: WorldType = WorldType.DEFAULT, worldTypePreset: String = "") extends LayerSpec {

  override val height: Int = 16 - topOffset - bottomOffset

  override val toLayer = VanillaDimensionLayer

  @SideOnly(Side.CLIENT)
  override def toGuiLayerEntry(parent: GuiLayersList): GuiLayerEntry = new GuiDimensionLayerEntry(parent, this)
}
