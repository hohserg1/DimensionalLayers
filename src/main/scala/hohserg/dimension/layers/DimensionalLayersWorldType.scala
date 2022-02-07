package hohserg.dimension.layers

import divinerpg.dimensions.iceika.{BiomeProviderIceika, IceikaChunkGenerator}
import hohserg.dimension.layers.DimensionalLayersWorldType.layers
import hohserg.dimension.layers.Layer.{FakeWorldLayer, SolidLayer}
import hohserg.dimension.layers.fake.world.FakeWorld
import hohserg.dimension.layers.gui.GuiSetupDimensionLayersPreset
import io.github.opencubicchunks.cubicchunks.api.util.IntRange
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import io.github.opencubicchunks.cubicchunks.api.worldgen.ICubeGenerator
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiCreateWorld
import net.minecraft.init.{Biomes, Blocks}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.BiomeProviderSingle
import net.minecraft.world.gen.ChunkGeneratorEnd
import net.minecraft.world.{World, WorldServer, WorldType}

class DimensionalLayersWorldType extends WorldType("dimension_layers") with ICubicWorldType {
  println("DimensionalLayersWorldType")

  override def onGUICreateWorldPress(): Unit = {
    println("onGUICreateWorldPress")
  }

  override def createCubeGenerator(world: World): ICubeGenerator = {
    new DimensionalLayersGenerator(world)
  }

  override def calculateGenerationHeightRange(worldServer: WorldServer): IntRange = {
    //println("calculateGenerationHeightRange", "pre", layers.keys)
    val range = IntRange.of(layers.keys.minBy(_.getMin).getMin * 16, layers.keys.maxBy(_.getMax).getMax * 16)
    //println("calculateGenerationHeightRange", range)
    range
  }

  override def hasCubicGeneratorForWorld(world: World): Boolean = {
    //println("hasCubicGeneratorForWorld", world)
    world.provider.getDimension == 0
  }

  override def isCustomizable: Boolean = true

  override def onCustomizeButton(mc: Minecraft, guiCreateWorld: GuiCreateWorld): Unit = {
    mc.displayGuiScreen(new GuiSetupDimensionLayersPreset(guiCreateWorld))

  }
}

object DimensionalLayersWorldType {

  lazy val mixAllDims: Map[IntRange, World => Layer] = DimensionLayersPreset.mixAllDims.toLayerMap

  lazy val layers: Map[IntRange, World => Layer] =
    Map(
      IntRange.of(15 + 1 + 15 + 1, 15 + 1 + 15 + 1 + 15) -> (world => FakeWorldLayer(FakeWorld(
        world.getSeed,
        enableMapFeatures = true,
        world => new IceikaChunkGenerator(world, world.getSeed),
        world => new BiomeProviderIceika(),
        hasSkyLight = false
      ))),

      IntRange.of(15 + 1, 15 + 1 + 15) -> (world => FakeWorldLayer(FakeWorld(
        world.getSeed,
        enableMapFeatures = true,
        world => new ChunkGeneratorEnd(world, world.getWorldInfo.isMapFeaturesEnabled, world.getSeed, new BlockPos(100, 50, 0)),
        world => new BiomeProviderSingle(Biomes.SKY), hasSkyLight = false
      ))),

      IntRange.of(0, 15) -> (world => FakeWorldLayer(FakeWorld(
        world.getSeed,
        enableMapFeatures = true,
        WorldType.DEFAULT.getChunkGenerator(_, ""),
        world => world.getWorldInfo.getTerrainType.getBiomeProvider(world),
        hasSkyLight = true
      ))),

      IntRange.of(-1, -1) -> (world => SolidLayer(Blocks.BEDROCK.getDefaultState))
    )
}
