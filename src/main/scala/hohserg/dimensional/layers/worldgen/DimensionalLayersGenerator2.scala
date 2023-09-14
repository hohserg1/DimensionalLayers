package hohserg.dimensional.layers.worldgen

import com.google.common.collect.Lists
import hohserg.dimensional.layers.DimensionLayersPreset
import io.github.opencubicchunks.cubicchunks.api.util.{Box, Coords}
import io.github.opencubicchunks.cubicchunks.api.world.{ICube, ICubicWorld}
import io.github.opencubicchunks.cubicchunks.api.worldgen.{CubePrimer, ICubeGenerator}
import io.github.opencubicchunks.cubicchunks.core.worldgen.WorldgenHangWatchdog
import net.minecraft.entity.EnumCreatureType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk

import java.util
import java.util.Random

class DimensionalLayersGenerator2(world: World) extends ICubeGenerator {
  val preset = DimensionLayersPreset(world.getWorldInfo.getGeneratorOptions)

  val layerAtCubeY: Map[Int, Layer] =
    preset.toLayerMap
      .map { case (range, layerFactory) => range -> layerFactory(world) }.flatMap { case (range, layer) =>
      for (i <- range.getMin to range.getMax)
        yield i -> layer
    }

  override def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer): CubePrimer = {
    layerAtCubeY.get(cubeY).foreach {
      case layer: VanillaLayer =>
        generateWithWatchdog(generateLayerTerrain, cubeX, cubeY, cubeZ, primer, layer)
        generateBiomes(cubeX, cubeZ, primer, layer)
      case layer: SolidLayer =>
        for {
          x <- 0 to 15
          y <- 0 to 15
          z <- 0 to 15
        } primer.setBlockState(x, y, z, layer.filler)
    }
    primer
  }

  private def generateBiomes(cubeX: Int, cubeZ: Int, primer: CubePrimer, layer: VanillaLayer): Unit = {
    layer.biomes = this.world.getBiomeProvider.getBiomes(layer.biomes, Coords.cubeToMinBlock(cubeX), Coords.cubeToMinBlock(cubeZ), 16, 16)
    for {
      localBiomeX <- 0 to 3
      localBiomeY <- 0 to 3
      localBiomeZ <- 0 to 3
    } primer.setBiome(localBiomeX, localBiomeY, localBiomeZ, layer.biomes((localBiomeX << 2) & 15 | ((localBiomeZ << 2) & 15) << 4))
  }

  private def generateWithWatchdog(generator: (Int, Int, Int, CubePrimer, VanillaLayer) => Unit, cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer, layer: VanillaLayer): Unit = {
    try {
      WorldgenHangWatchdog.startWorldGen()
      generator(cubeX, cubeY, cubeZ, primer, layer)
    } finally {
      WorldgenHangWatchdog.endWorldGen()
    }
  }

  private def generateLayerTerrain(cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer, layer: VanillaLayer): Unit = {
    val rand = new Random(world.getSeed)
    rand.setSeed(rand.nextInt ^ cubeX)
    rand.setSeed(rand.nextInt ^ cubeZ)

    if (layer.lastChunk == null || layer.lastChunk.x != cubeX || layer.lastChunk.z != cubeZ) {
      if (layer.lastChunk.x != cubeX || layer.lastChunk.z != cubeZ)
        println("bruh other column generation, expected", cubeX, cubeZ, "got", layer.lastChunk.x, layer.lastChunk.z)
      layer.lastChunk = layer.vanillaGenerator.generateChunk(cubeX, cubeZ)
    }

    if (!layer.optimizationHack) {
      layer.optimizationHack = true
      recursiveGeneration(cubeX, cubeY, cubeZ, layer)
      layer.optimizationHack = false
    }

    val storage = layer.lastChunk.getBlockStorageArray()((cubeY - layer.startCubeY) & 15)
    if (storage != null && !storage.isEmpty) {
      for {
        x <- 0 to 15
        y <- 0 to 15
        z <- 0 to 15
        block = storage.get(x, y, z)
      } primer.setBlockState(x, y, z, block)
    }
  }

  private def recursiveGeneration(cubeX: Int, cubeY: Int, cubeZ: Int, layer: VanillaLayer): Unit = {
    for (y <- (layer.startCubeY + 15) to layer.startCubeY by -1)
      if (y != cubeY)
        world.asInstanceOf[ICubicWorld].getCubeFromCubeCoords(cubeX, y, cubeZ)
  }

  override def generateColumn(chunk: Chunk): Unit = {

  }

  override def populate(cube: ICube): Unit = {
    //println("populate", cube.getX, cube.getY, cube.getZ)
  }

  override def getFullPopulationRequirements(cube: ICube): Box = ICubeGenerator.RECOMMENDED_FULL_POPULATOR_REQUIREMENT

  override def getPopulationPregenerationRequirements(cube: ICube): Box = ICubeGenerator.RECOMMENDED_GENERATE_POPULATOR_REQUIREMENT

  override def recreateStructures(cube: ICube): Unit = {

  }

  override def recreateStructures(chunk: Chunk): Unit = {

  }

  override def getPossibleCreatures(enumCreatureType: EnumCreatureType, blockPos: BlockPos): util.List[Biome.SpawnListEntry] = Lists.newArrayList()

  override def getClosestStructure(s: String, blockPos: BlockPos, b: Boolean): BlockPos = BlockPos.ORIGIN

  override def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int): CubePrimer = generateCube(cubeX, cubeY, cubeZ, new CubePrimer())
}
