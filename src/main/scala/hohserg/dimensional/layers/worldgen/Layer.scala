package hohserg.dimensional.layers.worldgen

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import hohserg.dimensional.layers.DimensionLayersPreset.DimensionLayerSpec
import hohserg.dimensional.layers.worldgen.proxy.{ProxyWorld, ShiftedBlockPos}
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world._
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.IChunkGenerator

import java.util.concurrent.TimeUnit

trait Layer {
  def startCubeY: Int

  def height: Int
}

class DimensionLayer(world: World, val spec: DimensionLayerSpec, val startCubeY: Int) extends Layer {
  val endCubeY: Int = startCubeY + spec.height - 1
  val startBlockY: Int = Coords.cubeToMinBlock(startCubeY)
  val endBlockY: Int = Coords.cubeToMaxBlock(endCubeY)
  val virtualStartBlockY: Int = 0
  val virtualEndBlockY: Int = Coords.cubeToMaxBlock(spec.height - 1)

  def shift(pos: BlockPos): ShiftedBlockPos = ShiftedBlockPos(pos, this)

  def markShifted(pos: BlockPos): ShiftedBlockPos = ShiftedBlockPos.markShifted(pos, this)

  val proxyWorld: ProxyWorld = new ProxyWorld(world, this)
  private val provider: WorldProvider = proxyWorld.provider
  val vanillaGenerator: IChunkGenerator = provider.createChunkGenerator()
  var optimizationHack: Boolean = false
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

  override def height: Int = spec.height
}

case class SolidLayer(filler: IBlockState, biome: Biome, startCubeY: Int, height: Int) extends Layer
