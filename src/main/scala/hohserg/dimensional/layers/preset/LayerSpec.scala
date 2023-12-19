package hohserg.dimensional.layers.preset

import net.minecraft.block.state.IBlockState
import net.minecraft.init.Biomes
import net.minecraft.world.biome.Biome
import net.minecraft.world.{DimensionType, WorldType}

sealed trait LayerSpec {
  def height: Int
}

case class DimensionLayerSpec(dimensionType: DimensionType,
                              seedOverride: Option[Long] = None,
                              topOffset: Int = 0, bottomOffset: Int = 0,
                              worldType: WorldType = WorldType.DEFAULT, worldTypePreset: String = "") extends LayerSpec {
  println("DimensionLayerSpec constructor")

  override def height: Int = 16 - topOffset - bottomOffset
}

case class SolidLayerSpec(filler: IBlockState, biome: Biome = Biomes.PLAINS, height: Int = 1) extends LayerSpec
