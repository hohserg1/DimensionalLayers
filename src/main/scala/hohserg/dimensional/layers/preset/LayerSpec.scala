package hohserg.dimensional.layers.preset

import hohserg.dimensional.layers.worldgen.proxy.{BaseWorldServer, ProxyWorld}
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Biomes
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.{DimensionType, World, WorldServer, WorldType}

sealed trait LayerSpec {
}

case class DimensionLayerSpec(dimensionType: DimensionType,
                              seedOverride: Option[Long] = None,
                              topOffset: Int = 0, bottomOffset: Int = 0,
                              worldType: WorldType = WorldType.DEFAULT, worldTypePreset: String = "") extends LayerSpec {

  def height: Int = 16 - topOffset - bottomOffset
}

case class SolidLayerSpec(filler: IBlockState, height: Int, biome: Biome = Biomes.PLAINS) extends LayerSpec

case class CubicWorldTypeLayerSpec(cubicWorldType: WorldType with ICubicWorldType, worldTypePreset: String = "",
                                   dimensionType1: DimensionType = DimensionType.OVERWORLD,
                                   seedOverride: Option[Long] = None
                                  ) extends LayerSpec {

  def rangeCube(original: World): (Int, Int) = {
    val range1 = cubicWorldType.calculateGenerationHeightRange(
      new BaseWorldServer(
        null,
        ProxyWorld.createLayerWorldInfo(original, seedOverride, cubicWorldType, worldTypePreset),
        dimensionType1.createDimension(),
        null
      ) {
        override def createChunkProvider(): IChunkProvider = ???

        override def isChunkLoaded(x: Int, z: Int, allowEmpty: Boolean): Boolean = ???
      }.asInstanceOf[WorldServer]
    )
    Coords.blockToCube(range1.getMin) -> Coords.blockToCube(range1.getMax)
  }
}