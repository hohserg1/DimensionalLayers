package hohserg.dimensional.layers.preset.spec

import hohserg.dimensional.layers.data.layer.solid.SolidLayer
import hohserg.dimensional.layers.gui.preset.list.{GuiLayerEntry, GuiLayersList, GuiSolidLayerEntry}
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Biomes
import net.minecraft.world.biome.Biome
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

case class SolidLayerSpec(filler: IBlockState, height: Int, biome: Biome = Biomes.PLAINS) extends LayerSpec {
  override val toLayer = SolidLayer

  @SideOnly(Side.CLIENT)
  override def toGuiLayerEntry(parent: GuiLayersList): GuiLayerEntry = new GuiSolidLayerEntry(parent, this)
}
