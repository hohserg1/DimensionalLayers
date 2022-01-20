package hohserg.dimensional.layers

import hohserg.dimensional.layers.DimensionalLayersWorldType.layers
import hohserg.dimensional.layers.Layer.{FakeWorldLayer, SolidLayer}
import hohserg.dimensional.layers.fake.world.FakeWorld
import io.github.opencubicchunks.cubicchunks.api.util.IntRange
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import io.github.opencubicchunks.cubicchunks.api.worldgen.ICubeGenerator
import net.minecraft.init.{Biomes, Blocks}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.BiomeProviderSingle
import net.minecraft.world.gen.ChunkGeneratorEnd
import net.minecraft.world.{World, WorldServer, WorldType}

class DimensionalLayersWorldType extends WorldType("dimension_layers") with ICubicWorldType {
  override def createCubeGenerator(world: World): ICubeGenerator = new DimensionalLayersGenerator(world)

  override def calculateGenerationHeightRange(worldServer: WorldServer): IntRange = {
    println("calculateGenerationHeightRange", "pre", layers.keys)
    val range = IntRange.of(layers.keys.minBy(_.getMin).getMin * 16, layers.keys.maxBy(_.getMax).getMax * 16)
    println("calculateGenerationHeightRange", range)
    range
  }

  override def hasCubicGeneratorForWorld(world: World): Boolean = world.provider.getDimension == 0
}

object DimensionalLayersWorldType {
  lazy val layers: Map[IntRange, World => Layer] =
    Map(
      IntRange.of(17, 17 + 16) -> (world => FakeWorldLayer(FakeWorld(world.getSeed, enableMapFeatures = true, world => new ChunkGeneratorEnd(world, world.getWorldInfo.isMapFeaturesEnabled, world.getSeed, new BlockPos(100, 50, 0)), world => new BiomeProviderSingle(Biomes.SKY), hasSkyLight = false))),
      IntRange.of(0, 16) -> (world => FakeWorldLayer(FakeWorld(world.getSeed, enableMapFeatures = true, WorldType.DEFAULT.getChunkGenerator(_, ""), world => world.getWorldInfo.getTerrainType.getBiomeProvider(world), hasSkyLight = true))),
      IntRange.of(-1, -1) -> (world => SolidLayer(Blocks.BEDROCK.getDefaultState))
    )
}
