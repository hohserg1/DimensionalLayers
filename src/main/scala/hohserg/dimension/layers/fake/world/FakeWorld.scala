package hohserg.dimension.layers.fake.world

import java.util.function

import hohserg.dimension.layers._
import net.minecraft.block.state.IBlockState
import net.minecraft.profiler.Profiler
import net.minecraft.util.math.{BlockPos, ChunkPos}
import net.minecraft.world._
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraft.world.storage.WorldInfo

import scala.collection.mutable

class FakeWorld(info: WorldInfo, val chunkGeneratorFactory: World => IChunkGenerator, val biomeProviderFactory: World => BiomeProvider, hasSkyLight: Boolean, val capacity: Int = 200 * 200) extends World(
  new FakeSaveHandler(info),
  info,
  new FakeWorldProvider(biomeProviderFactory, hasSkyLight),
  new Profiler,
  false
) with FakeCubicWorld {
  provider.setWorld(this)
  chunkProvider = createChunkProvider()

  var populateMode = false
  val populateChanges = new LRUCache[ChunkPos, mutable.HashMap[BlockPos, IBlockState]](8 * 8)

  override def setBlockState(pos: BlockPos, newState: IBlockState, flags: Int): Boolean = {
    if (populateMode) {
      val key = new ChunkPos(pos)
      populateChanges.computeIfAbsent(key, new function.Function[ChunkPos, mutable.HashMap[BlockPos, IBlockState]] {
        override def apply(t: ChunkPos) = new mutable.HashMap[BlockPos, IBlockState]
      }).put(pos, newState)
      true
    } else
      super.setBlockState(pos, newState, flags)
  }

  override def getBlockState(pos: BlockPos): IBlockState = {
    if (populateMode) {
      val key = new ChunkPos(pos)
      populateChanges.computeIfAbsent(key, new function.Function[ChunkPos, mutable.HashMap[BlockPos, IBlockState]] {
        override def apply(t: ChunkPos) = new mutable.HashMap[BlockPos, IBlockState]
      }).getOrElse(pos, super.getBlockState(pos))
    } else
      super.getBlockState(pos)
  }

  def getPopulatedChanges(x: Int, z: Int): Map[BlockPos, IBlockState] = {
    populateMode = true
    chunkProvider.asInstanceOf[FakeChunkProvider].populate(x, z)
    populateMode = false

    val minX = x << 4
    val minZ = z << 4
    val maxX = (x << 4) + 15
    val maxZ = (z << 4) + 15
    val key = new ChunkPos(x, z)
    if (populateChanges.containsKey(key))
      populateChanges.get(key).toMap
    else
      Map()
  }

  override def createChunkProvider(): IChunkProvider = new FakeChunkProvider(this)

  override def getSeed: Long = super.getSeed

  override def isChunkLoaded(x: Int, z: Int, allowEmpty: Boolean): Boolean = chunkProvider.isChunkGeneratedAt(x, z)
}

object FakeWorld {
  def apply(seed: Long, enableMapFeatures: Boolean, chunkGeneratorFactory: World => IChunkGenerator, biomeProviderFactory: World => BiomeProvider, hasSkyLight: Boolean): FakeWorld =
    new FakeWorld(new WorldInfo(new WorldSettings(seed, GameType.CREATIVE, enableMapFeatures, false, WorldType.DEFAULT), "world"), chunkGeneratorFactory, biomeProviderFactory, hasSkyLight)
}
