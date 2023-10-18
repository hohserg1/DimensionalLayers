package hohserg.dimensional.layers.worldgen

import com.google.common.collect.ImmutableList
import hohserg.dimensional.layers.DimensionLayersPreset
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

class DimensionalLayersGenerator2(world: World) extends ICubeGenerator {
  val preset = DimensionLayersPreset(world.getWorldInfo.getGeneratorOptions)

  val cubicWorld = world.asInstanceOf[ICubicWorldInternal]

  val layerAtCubeY: Map[Int, Layer] =
    preset.toLayerMap
      .map { case (range, layerFactory) => range -> layerFactory(world) }.flatMap { case (range, layer) =>
      for (i <- range.getMin to range.getMax)
        yield i -> layer
    }

  private def generateWithWatchdog[BlockStateAcceptor](generator: (Int, Int, Int, BlockStateAcceptor, VanillaLayer) => Unit, cubeX: Int, cubeY: Int, cubeZ: Int, target: BlockStateAcceptor, layer: VanillaLayer): Unit = {
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
    layer.biomes = layer.proxyWorld.getBiomeProvider.getBiomes(layer.biomes, Coords.cubeToMinBlock(cubeX), Coords.cubeToMinBlock(cubeZ), 16, 16)
    for {
      localBiomeX <- 0 to 3
      localBiomeY <- 0 to 3
      localBiomeZ <- 0 to 3
    } primer.setBiome(localBiomeX, localBiomeY, localBiomeZ, layer.biomes((localBiomeX << 2) & 15 | ((localBiomeZ << 2) & 15) << 4))
  }

  private def generateLayerTerrain(cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer, layer: VanillaLayer): Unit = {
    val rand = new Random(world.getSeed)
    rand.setSeed(rand.nextInt ^ cubeX)
    rand.setSeed(rand.nextInt ^ cubeZ)

    val chunk = layer.lastChunks.get(cubeX -> cubeZ)

    if (layer.spec.dimensionType.getName == "Aether")
      println("Aether")

    if (!layer.optimizationHack) {
      layer.optimizationHack = true
      recursiveGeneration(cubeX, cubeY, cubeZ, layer)
      layer.optimizationHack = false
    }

    val storage = chunk.getBlockStorageArray()((cubeY - layer.startCubeY) & 15)
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
    for (y <- (layer.startCubeY + layer.height - 1) to layer.startCubeY by -1)
      if (y != cubeY)
        world.asInstanceOf[ICubicWorld].getCubeFromCubeCoords(cubeX, y, cubeZ)
  }

  override def populate(cube: ICube): Unit = {
    layerAtCubeY.get(cube.getY).foreach {
      case layer: VanillaLayer =>
        generateWithWatchdog(generateLayerFeatures, cube.getX, cube.getY, cube.getZ, cube, layer)
      case _ =>
    }
  }

  private def generateLayerFeatures(cubeX: Int, cubeY: Int, cubeZ: Int, cube: ICube, layer: VanillaLayer): Unit = {
    val rand = new Random(world.getSeed)
    rand.setSeed(rand.nextInt() ^ cubeX)
    rand.setSeed(rand.nextInt() ^ cubeZ)
    rand.setSeed(rand.nextInt() ^ cubeY)

    markColumnPopulated(cubeX, cubeZ, layer)

    try {
      layer.vanillaGenerator.populate(cubeX, cubeZ)
    } catch {
      case ex: IllegalArgumentException =>
        val stack = ex.getStackTrace
        if (stack == null || stack.length < 1 || stack(0).getClassName != classOf[Random].getName || stack(0).getMethodName != "nextInt")
          throw ex
        CubicChunks.LOGGER.error("Error while populating. Likely known mod issue, ignoring...", ex)
    }
    applyModGenerators(cubeX, cubeZ, layer)
  }

  def applyModGenerators(x: Int, z: Int, layer: VanillaLayer): Unit = {
    if (IGameRegistry.getSortedGeneratorList == null)
      IGameRegistry.computeGenerators()

    val generators = IGameRegistry.getSortedGeneratorList.asScala

    val worldSeed = world.getSeed
    val fmlRandom = new Random(worldSeed)
    val xSeed = fmlRandom.nextLong() >> 3L
    val zSeed = fmlRandom.nextLong() >> 3L
    val chunkSeed = xSeed * x + zSeed * z ^ worldSeed


    for (generator <- generators) {
      fmlRandom.setSeed(chunkSeed)
      generator.generate(fmlRandom, x, z, layer.proxyWorld, layer.vanillaGenerator, layer.proxyWorld.getChunkProvider)
    }
  }

  private def markColumnPopulated(cubeX: Int, cubeZ: Int, layer: VanillaLayer): Unit = {
    for (y <- (layer.startCubeY + layer.height - 1) to layer.startCubeY by -1) {
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
      case layer: VanillaLayer =>
        val i = cube.getY - layer.startCubeY
        new Box(-1, -i, -1, 0, layer.height - i - 1, 0)
    }.getOrElse(ICubeGenerator.NO_REQUIREMENT)
  }

  val LayeredGeneratePopulatorRequirement = new Box(1, 15, 1, 0, 0, 0)

  override def getPopulationPregenerationRequirements(cube: ICube): Box = {
    layerAtCubeY.get(cube.getY).collect {
      case layer: VanillaLayer =>
        val i = cube.getY - layer.startCubeY
        new Box(0, -i, 0, 1, layer.height - i - 1, 1)
    }.getOrElse(ICubeGenerator.NO_REQUIREMENT)
  }

  override def generateColumn(chunk: Chunk): Unit = ()

  override def recreateStructures(cube: ICube): Unit =
    layerAtCubeY.get(cube.getY).foreach {
      case layer: VanillaLayer =>
        layer.vanillaGenerator.recreateStructures(layer.proxyWorld.getChunk(cube.getX, cube.getZ), cube.getX, cube.getZ)
      case _ =>
    }


  override def recreateStructures(chunk: Chunk): Unit = ()


  override def getPossibleCreatures(enumCreatureType: EnumCreatureType, blockPos: BlockPos): util.List[Biome.SpawnListEntry] =
    layerAtCubeY.get(Coords.blockToCube(blockPos.getY)).collect {
      case layer: VanillaLayer =>
        layer.vanillaGenerator.getPossibleCreatures(enumCreatureType, blockPos.down(layer.startBlockY))
    }.getOrElse(ImmutableList.of())

  override def getClosestStructure(name: String, blockPos: BlockPos, findUnexplored: Boolean): BlockPos =
    layerAtCubeY.get(Coords.blockToCube(blockPos.getY)).collect {
      case layer: VanillaLayer =>
        layer.vanillaGenerator.getNearestStructurePos(this.world, name, blockPos, findUnexplored)
    }.orNull

  override def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int): CubePrimer = generateCube(cubeX, cubeY, cubeZ, new CubePrimer())
}
