package hohserg.dimensional.layers.data.layer.otg

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import hohserg.dimensional.layers.data.layer.base.{BiomeGeneratorHelper, DimensionalGenerator}
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer
import hohserg.dimensional.layers.{CCWorldServer, Main}
import io.github.opencubicchunks.cubicchunks.api.world.ICube
import io.github.opencubicchunks.cubicchunks.api.worldgen.CubePrimer
import io.github.opencubicchunks.cubicchunks.core.asm.mixin.core.common.IGameRegistry
import net.minecraft.entity.EnumCreatureType
import net.minecraft.init.Biomes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldProvider
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.IChunkGenerator

import java.util
import java.util.Random
import java.util.concurrent.TimeUnit
import scala.collection.JavaConverters._

class OpenTerrainGeneratorGenerator(original: CCWorldServer, val layer: OpenTerrainGeneratorLayer) extends DimensionalGenerator with BiomeGeneratorHelper {
  override type L = OpenTerrainGeneratorLayer
  override type BiomeContext = Array[Biome]

  override val proxyWorld = ProxyWorldServer(original, layer, this)

  private val provider: WorldProvider = proxyWorld.provider
  val vanillaGenerator: IChunkGenerator = provider.createChunkGenerator()
  var biomes: Array[Biome] = _

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

  override def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer): CubePrimer = {
    val chunk = lastChunks.get(cubeX -> cubeZ)

    val index = (cubeY - bounds.realStartCubeY) & 15

    val storage = chunk.getBlockStorageArray()(index)
    if (storage != null && !storage.isEmpty) {
      for {
        x <- 0 to 15
        y <- 0 to 15
        z <- 0 to 15
        block = storage.get(x, y, z)
      } primer.setBlockState(x, y, z, block)
    }


    if (biomes == null)
      biomes = new Array[Biome](16 * 16)
    val rawBiomes = chunk.getBiomeArray
    for (i <- rawBiomes.indices) {
      biomes(i) = Biome.getBiome(rawBiomes(i), Biomes.DEFAULT);
    }
    generateBiomes(primer, biomes)

    primer
  }

  override protected def calcBiome(localBiomeX: Int, localBiomeY: Int, localBiomeZ: Int, biomes: Array[Biome]): Biome =
    biomes((localBiomeX << 2) & 15 | ((localBiomeZ << 2) & 15) << 4)

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

  override def getPossibleCreaturesNullable(creatureType: EnumCreatureType, realPos: BlockPos): util.List[Biome.SpawnListEntry] =
    vanillaGenerator.getPossibleCreatures(creatureType, bounds.markShifted(realPos).unshift)

  override def recreateStructures(cube: ICube): Unit =
    vanillaGenerator.recreateStructures(proxyWorld.getChunk(cube.getX, cube.getZ), cube.getX, cube.getZ)

  override def getNearestStructurePos(name: String, blockPos: BlockPos, findUnexplored: Boolean): Option[BlockPos] =
    Option(vanillaGenerator.getNearestStructurePos(original, name, blockPos, findUnexplored))
}
