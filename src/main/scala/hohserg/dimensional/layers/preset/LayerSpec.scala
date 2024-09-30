package hohserg.dimensional.layers.preset

import hohserg.dimensional.layers.CCWorld
import hohserg.dimensional.layers.data.layer.base.Layer
import hohserg.dimensional.layers.data.layer.cubic_world_type.CubicWorldTypeLayer
import hohserg.dimensional.layers.data.layer.solid.SolidLayer
import hohserg.dimensional.layers.data.layer.vanilla_dimension.VanillaDimensionLayer
import hohserg.dimensional.layers.preset.CubicWorldTypeLayerSpec.dummyWorld
import hohserg.dimensional.layers.worldgen.proxy.server.BaseWorldServer
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Biomes
import net.minecraft.world._
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.storage.WorldInfo

sealed trait LayerSpec {
  def height: Int

  def toLayer: (Int, this.type, CCWorld) => Layer

  def toLayer(startFromCubeY: Int, original: CCWorld): Layer = toLayer(startFromCubeY, this, original)

}

case class DimensionLayerSpec(dimensionType: DimensionType,
                              seedOverride: Option[Long] = None,
                              topOffset: Int = 0, bottomOffset: Int = 0,
                              worldType: WorldType = WorldType.DEFAULT, worldTypePreset: String = "") extends LayerSpec {

  override val height: Int = 16 - topOffset - bottomOffset

  override val toLayer = VanillaDimensionLayer
}

case class SolidLayerSpec(filler: IBlockState, height: Int, biome: Biome = Biomes.PLAINS) extends LayerSpec {
  override val toLayer = SolidLayer
}

case class CubicWorldTypeLayerSpec(cubicWorldType: WorldType with ICubicWorldType, worldTypePreset: String = "",
                                   dimensionType1: DimensionType = DimensionType.OVERWORLD,
                                   seedOverride: Option[Long] = None
                                  ) extends LayerSpec {

  def rangeCube(originalWorld: CCWorld): (Int, Int) =
    rangeCube(originalWorld.getWorldInfo.getGameType, originalWorld.getWorldInfo.isMapFeaturesEnabled)

  def rangeCube(gameType: GameType, isMapFeaturesEnabled: Boolean): (Int, Int) = {
    val range1 = cubicWorldType.calculateGenerationHeightRange(
      dummyWorld(this, gameType, isMapFeaturesEnabled)
    )
    Coords.blockToCube(range1.getMin) -> Coords.blockToCube(range1.getMax)
  }

  override val toLayer = CubicWorldTypeLayer

  override val height: Int = {
    val (virtualStartCubeY, virtualEndCubeY) = rangeCube(GameType.SURVIVAL, isMapFeaturesEnabled = true)
    virtualEndCubeY - virtualStartCubeY + 1
  }
}

object CubicWorldTypeLayerSpec {

  def dummyWorld(spec: CubicWorldTypeLayerSpec, gameType: GameType = GameType.SURVIVAL, isMapFeaturesEnabled: Boolean = true): WorldServer = {
    new BaseWorldServer(
      null,
      new WorldInfo(
        new WorldSettings(
          spec.seedOverride.getOrElse(0),
          gameType,
          isMapFeaturesEnabled,
          false,
          spec.cubicWorldType
        ).setGeneratorOptions(spec.worldTypePreset),
        "New World---"
      ),
      spec.dimensionType1.createDimension(),
      null
    ) {
      override def createChunkProvider(): IChunkProvider = ???

      override def isChunkLoaded(x: Int, z: Int, allowEmpty: Boolean): Boolean = ???
    }.asInstanceOf[WorldServer]
  }
}