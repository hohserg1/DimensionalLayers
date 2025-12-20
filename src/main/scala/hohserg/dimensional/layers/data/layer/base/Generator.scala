package hohserg.dimensional.layers.data.layer.base

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.google.common.collect.ImmutableList
import hohserg.dimensional.layers.data.layer.vanilla_dimension.VanillaDimensionLayer
import hohserg.dimensional.layers.preset.spec.{CubeOffsets, LayerSpec}
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer.createLayerWorldInfo
import hohserg.dimensional.layers.{CCWorldServer, Main}
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import io.github.opencubicchunks.cubicchunks.api.world.ICube
import io.github.opencubicchunks.cubicchunks.api.worldgen.CubePrimer
import io.github.opencubicchunks.cubicchunks.core.asm.mixin.core.common.IGameRegistry
import net.minecraft.entity.EnumCreatureType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.IChunkGenerator
import net.minecraft.world.{WorldProvider, WorldType}

import java.util
import java.util.Random
import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters.*

trait Generator {
  type L <: Layer

  def layer: L

  def needGenerateTotalColumn: Boolean = false

  def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer): CubePrimer

  def populateCube(cube: ICube): Unit

  def recreateStructures(cube: ICube): Unit

  def getPossibleCreatures(creatureType: EnumCreatureType, realPos: BlockPos): util.List[Biome.SpawnListEntry] = {
    Option(getPossibleCreaturesNullable(creatureType, realPos)).getOrElse(ImmutableList.of())
  }

  def getPossibleCreaturesNullable(creatureType: EnumCreatureType, realPos: BlockPos): util.List[Biome.SpawnListEntry]

  def getNearestStructurePos(name: String, blockPos: BlockPos, findUnexplored: Boolean): Option[BlockPos] = None

}

trait DimensionalGenerator extends Generator {
  override type L <: DimensionalLayer

  def proxyWorld: ProxyWorldServer
}

trait BiomeGeneratorHelper {
  type BiomeContext

  protected def calcBiome(localBiomeX: Int, localBiomeY: Int, localBiomeZ: Int, context: BiomeContext): Biome

  protected def generateBiomes(primer: CubePrimer, context: BiomeContext): Unit = {
    for {
      localBiomeX <- 0 to 3
      localBiomeY <- 0 to 3
      localBiomeZ <- 0 to 3
    } primer.setBiome(localBiomeX, localBiomeY, localBiomeZ, calcBiome(localBiomeX, localBiomeY, localBiomeZ, context))
  }

}

abstract class VanillaDimensionGeneratorBase[LL <: VanillaDimensionLayerBase](original: CCWorldServer, val layer: LL, specOffsets: CubeOffsets) extends DimensionalGenerator with BiomeGeneratorHelper {
  override type L = LL
  override type BiomeContext = Array[Biome]

  def seedOverride: Option[Long]

  def worldType: WorldType

  def worldTypePreset: String
  
  def beforeInitWorld(proxyWorld: ProxyWorldServer): Unit = {
    
  }

  override val proxyWorld = new ProxyWorldServer(
    original,
    layer,
    this,
    createLayerWorldInfo(original, seedOverride, worldType, worldTypePreset)
  ){
    override def initWorld(): Unit = {
      beforeInitWorld(this)
      super.initWorld()
    }
  }
  private val provider: WorldProvider = proxyWorld.provider
  val vanillaGenerator: IChunkGenerator = provider.createChunkGenerator()
  var biomes: Array[Biome] = null

  val lastChunks: LoadingCache[(Int, Int), Chunk] =
    CacheBuilder.newBuilder()
                .maximumSize(200)
                .expireAfterAccess(60, TimeUnit.SECONDS)
                .build(new CacheLoader[(Int, Int), Chunk] {
                  override def load(key: (Int, Int)): Chunk = {
                    val r = vanillaGenerator.generateChunk(key._1, key._2)
                    r.onLoad()
                    r
                  }
                })

  override def needGenerateTotalColumn: Boolean = true

  override def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer): CubePrimer = {
    val chunk = lastChunks.get(cubeX -> cubeZ)

    //took from io.github.opencubicchunks.cubicchunks.core.asm.mixin.core.common.MixinChunk_Cubes#init_getStorage
    val index = ((cubeY - layer.bounds.realStartCubeY + specOffsets.bottomOffset) & 15) - Coords.blockToCube(proxyWorld.getMinHeight)

    val storage = chunk.getBlockStorageArray()(index)
    if (storage != null && !storage.isEmpty) {
      for {
        x <- 0 to 15
        y <- 0 to 15
        z <- 0 to 15
        block = storage.get(x, y, z)
        block2 = layer.blockReplacements.getOrElse(block, block)
      } primer.setBlockState(x, y, z, block2)
    }

    biomes = proxyWorld.getBiomeProvider.getBiomes(biomes, Coords.cubeToMinBlock(cubeX), Coords.cubeToMinBlock(cubeZ), 16, 16)
    generateBiomes(primer, biomes)

    primer
  }

  override protected def calcBiome(localBiomeX: Int, localBiomeY: Int, localBiomeZ: Int, biomes: Array[Biome]): Biome = {
    biomes((localBiomeX << 2) & 15 | ((localBiomeZ << 2) & 15) << 4)
  }

  override def populateCube(cube: ICube): Unit = {
    try {
      vanillaGenerator.populate(cube.getX, cube.getZ)
      applyModGenerators(cube.getX, cube.getZ)
    } catch {
      case ex: IllegalArgumentException =>
        val stack = ex.getStackTrace
        if (stack == null || stack.length < 1 || stack(0).getClassName != classOf[Random].getName || stack(0).getMethodName != "nextInt")
          throw ex
        Main.sided.printWarning("Error while populating. Likely known mod issue, ignoring...", ex)
    }
  }

  def applyModGenerators(x: Int, z: Int): Unit = {
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
      generator.generate(fmlRandom, x, z, proxyWorld, vanillaGenerator, proxyWorld.getChunkProvider)
    }
  }

  override def getPossibleCreaturesNullable(creatureType: EnumCreatureType, realPos: BlockPos): util.List[Biome.SpawnListEntry] = {
    vanillaGenerator.getPossibleCreatures(creatureType, layer.bounds.markShifted(realPos).unshift)
  }

  override def recreateStructures(cube: ICube): Unit = {
    vanillaGenerator.recreateStructures(proxyWorld.getChunk(cube.getX, cube.getZ), cube.getX, cube.getZ)
  }

  override def getNearestStructurePos(name: String, blockPos: BlockPos, findUnexplored: Boolean): Option[BlockPos] = {
    Option(vanillaGenerator.getNearestStructurePos(original, name, blockPos, findUnexplored))
  }
}
