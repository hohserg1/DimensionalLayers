package hohserg.dimensional.layers.worldgen

import com.google.common.collect.ImmutableList
import hohserg.dimensional.layers.preset.DimensionalLayersPreset
import io.github.opencubicchunks.cubicchunks.api.util.{Box, Coords}
import io.github.opencubicchunks.cubicchunks.api.world.{ICube, ICubicWorld}
import io.github.opencubicchunks.cubicchunks.api.worldgen.{CubePrimer, ICubeGenerator}
import io.github.opencubicchunks.cubicchunks.core.CubicChunks
import io.github.opencubicchunks.cubicchunks.core.asm.mixin.ICubicWorldInternal
import io.github.opencubicchunks.cubicchunks.core.asm.mixin.core.common.IGameRegistry
import io.github.opencubicchunks.cubicchunks.core.worldgen.WorldgenHangWatchdog
import net.minecraft.entity.EnumCreatureType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk

import java.util
import java.util.Random
import scala.collection.JavaConverters._

class DimensionalLayersGenerator(original: World) extends ICubeGenerator {
  val preset = DimensionalLayersPreset(original.getWorldInfo.getGeneratorOptions)

  val cubicWorld = original.asInstanceOf[ICubicWorldInternal]

  val layerAtCubeY: Map[Int, Layer] = preset.toLayerMap(original)

  private def generateWithWatchdog[BlockStateAcceptor](generator: (Int, Int, Int, BlockStateAcceptor, DimensionLayer) => Unit, cubeX: Int, cubeY: Int, cubeZ: Int, target: BlockStateAcceptor, layer: DimensionLayer): Unit = {
    try {
      WorldgenHangWatchdog.startWorldGen()
      generator(cubeX, cubeY, cubeZ, target, layer)
    } catch {
      case e: Throwable =>
        CubicChunks.LOGGER.error("DimensionalLayersGenerator2#generateWithWatchdog error", e)
    } finally {
      WorldgenHangWatchdog.endWorldGen()
    }
  }

  override def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer): CubePrimer = {
    layerAtCubeY.get(cubeY).foreach {
      case layer: DimensionLayer =>
        generateWithWatchdog(generateLayerTerrain, cubeX, cubeY, cubeZ, primer, layer)
        layer.biomes = layer.proxyWorld.getBiomeProvider.getBiomes(layer.biomes, Coords.cubeToMinBlock(cubeX), Coords.cubeToMinBlock(cubeZ), 16, 16)
        generateBiomes(primer, (localBiomeX, _, localBiomeZ) => layer.biomes((localBiomeX << 2) & 15 | ((localBiomeZ << 2) & 15) << 4))

      case layer: SolidLayer =>
        for {
          x <- 0 to 15
          y <- 0 to 15
          z <- 0 to 15
        } primer.setBlockState(x, y, z, layer.filler)
        generateBiomes(primer, (_, _, _) => layer.biome)
    }
    primer
  }

  private def generateBiomes(primer: CubePrimer, calcBiome: (Int, Int, Int) => Biome): Unit = {
    for {
      localBiomeX <- 0 to 3
      localBiomeY <- 0 to 3
      localBiomeZ <- 0 to 3
    } primer.setBiome(localBiomeX, localBiomeY, localBiomeZ, calcBiome(localBiomeX, localBiomeY, localBiomeZ))
  }

  private def generateDimensionLayerTerrain(cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer, layer: DimensionLayer): Unit = {
    val chunk = layer.lastChunks.get(cubeX -> cubeZ)

    if (!layer.optimizationHack) {
      layer.optimizationHack = true
      recursiveGeneration(cubeX, cubeY, cubeZ, layer)
      layer.optimizationHack = false
    }

    val storage = chunk.getBlockStorageArray()((cubeY - layer.realStartCubeY + layer.spec.bottomOffset) & 15)
    if (storage != null && !storage.isEmpty) {
      for {
        x <- 0 to 15
        y <- 0 to 15
        z <- 0 to 15
        block = storage.get(x, y, z)
      } primer.setBlockState(x, y, z, block)
    }
  }

  private def recursiveGeneration(cubeX: Int, cubeY: Int, cubeZ: Int, layer: DimensionLayer): Unit = {
    for (y <- (layer.realStartCubeY + layer.height - 1) to layer.realStartCubeY by -1)
      if (y != cubeY)
        original.asInstanceOf[ICubicWorld].getCubeFromCubeCoords(cubeX, y, cubeZ)
  }

  override def populate(cube: ICube): Unit = {
    layerAtCubeY.get(cube.getY).foreach {
      case layer: DimensionLayer =>
        generateWithWatchdog(generateLayerFeatures, cube.getX, cube.getY, cube.getZ, cube, layer)
      case _ =>
    }
  }

  private def generateLayerFeatures(cubeX: Int, cubeY: Int, cubeZ: Int, cube: ICube, layer: DimensionLayer): Unit = {
    markColumnPopulated(cubeX, cubeZ, layer)

    try {
      layer.vanillaGenerator.populate(cubeX, cubeZ)
      applyModGenerators(cubeX, cubeZ, layer)
    } catch {
      case ex: IllegalArgumentException =>
        val stack = ex.getStackTrace
        if (stack == null || stack.length < 1 || stack(0).getClassName != classOf[Random].getName || stack(0).getMethodName != "nextInt")
          throw ex
        CubicChunks.LOGGER.error("Error while populating. Likely known mod issue, ignoring...", ex)
    }
  }

  def applyModGenerators(x: Int, z: Int, layer: DimensionLayer): Unit = {
    if (IGameRegistry.getSortedGeneratorList == null)
      IGameRegistry.computeGenerators()

    val generators = IGameRegistry.getSortedGeneratorList.asScala

    val worldSeed = original.getSeed
    val fmlRandom = new Random(worldSeed)
    val xSeed = fmlRandom.nextLong() >> 3L
    val zSeed = fmlRandom.nextLong() >> 3L
    val chunkSeed = xSeed * x + zSeed * z ^ worldSeed


    for (generator <- generators) {
      fmlRandom.setSeed(chunkSeed)
      generator.generate(fmlRandom, x, z, layer.proxyWorld, layer.vanillaGenerator, layer.proxyWorld.getChunkProvider)
    }
  }

  private def markColumnPopulated(cubeX: Int, cubeZ: Int, layer: DimensionLayer): Unit = {
    for (y <- (layer.realStartCubeY + layer.height - 1) to layer.realStartCubeY by -1) {
      cubicWorld.getCubeFromCubeCoords(cubeX, y, cubeZ).setPopulated(true)
    }
  }

  val layeredFullPopulatorRequirement: IndexedSeq[Box] =
    for {
      y <- 0 to -15 by -1
    } yield
      new Box(-1, y, -1, 0, 0, 0)


  override def getFullPopulationRequirements(cube: ICube): Box = {
    layerAtCubeY.get(cube.getY).collect {
      case layer: DimensionLayer =>
        val i = cube.getY - layer.realStartCubeY
        new Box(-1, -i, -1, 0, layer.height - i - 1, 0)
    }.getOrElse(ICubeGenerator.NO_REQUIREMENT)
  }

  val LayeredGeneratePopulatorRequirement = new Box(1, 15, 1, 0, 0, 0)

  override def getPopulationPregenerationRequirements(cube: ICube): Box = {
    layerAtCubeY.get(cube.getY).collect {
      case layer: DimensionLayer =>
        val i = cube.getY - layer.realStartCubeY
        new Box(0, -i, 0, 1, layer.height - i - 1, 1)
    }.getOrElse(ICubeGenerator.NO_REQUIREMENT)
  }

  override def generateColumn(chunk: Chunk): Unit = ()

  override def recreateStructures(cube: ICube): Unit =
    layerAtCubeY.get(cube.getY).foreach {
      case layer: DimensionLayer =>
        layer.vanillaGenerator.recreateStructures(layer.proxyWorld.getChunk(cube.getX, cube.getZ), cube.getX, cube.getZ)
      case _ =>
    }


  override def recreateStructures(chunk: Chunk): Unit = ()


  override def getPossibleCreatures(enumCreatureType: EnumCreatureType, blockPos: BlockPos): util.List[Biome.SpawnListEntry] =
    layerAtCubeY.get(Coords.blockToCube(blockPos.getY)).collect {
      case layer: DimensionLayer =>
        layer.vanillaGenerator.getPossibleCreatures(enumCreatureType, blockPos.down(layer.realStartBlockY))
    }.getOrElse(ImmutableList.of())

  override def getClosestStructure(name: String, blockPos: BlockPos, findUnexplored: Boolean): BlockPos =
    layerAtCubeY.get(Coords.blockToCube(blockPos.getY)).collect {
      case layer: DimensionLayer =>
        layer.vanillaGenerator.getNearestStructurePos(this.original, name, blockPos, findUnexplored)
    }.orNull

  override def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int): CubePrimer = generateCube(cubeX, cubeY, cubeZ, new CubePrimer())
}
