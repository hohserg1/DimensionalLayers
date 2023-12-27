package hohserg.dimensional.layers.worldgen

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.google.common.collect.ImmutableList
import hohserg.dimensional.layers.preset.{CubicWorldTypeLayerSpec, DimensionLayerSpec}
import hohserg.dimensional.layers.worldgen.proxy.{ProxyWorld, ShiftedBlockPos}
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EnumCreatureType
import net.minecraft.util.math.BlockPos
import net.minecraft.world._
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.IChunkGenerator

import java.util
import java.util.concurrent.TimeUnit

sealed trait Layer {
  def realStartCubeY: Int

  def height: Int
}

sealed trait BaseDimensionLayer extends Layer {
  def proxyWorld: ProxyWorld

  def realEndCubeY: Int = realStartCubeY + height - 1

  def realStartBlockY: Int = Coords.cubeToMinBlock(realStartCubeY)

  def realEndBlockY: Int = Coords.cubeToMaxBlock(realEndCubeY)

  def virtualStartCubeY: Int

  def virtualEndCubeT: Int

  def virtualStartBlockY: Int = Coords.cubeToMinBlock(virtualStartCubeY)

  def virtualEndBlockY: Int = Coords.cubeToMaxBlock(virtualEndCubeT)

  def dimensionType: DimensionType

  def shift(pos: BlockPos): ShiftedBlockPos = ShiftedBlockPos(pos, this)

  def markShifted(pos: BlockPos): ShiftedBlockPos = ShiftedBlockPos.markShifted(pos, this)

  def isInLayer(y: Int): Boolean =
    virtualStartBlockY <= y && y <= virtualEndBlockY

  def executeInLayer[A](pos: BlockPos, f: ShiftedBlockPos => A, default: A): A = {
    val p = shift(pos)
    if (p.isInLayer)
      f(p)
    else
      default
  }

  def getPossibleCreatures(creatureType: EnumCreatureType, localPos: BlockPos): util.List[Biome.SpawnListEntry]
}

class DimensionLayer(original: World, val spec: DimensionLayerSpec, val realStartCubeY: Int) extends BaseDimensionLayer {
  val virtualStartCubeY: Int = spec.bottomOffset
  val virtualEndCubeT: Int = 16 - spec.topOffset - 1

  val proxyWorld = ProxyWorld(original, this)
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

  override def dimensionType: DimensionType = spec.dimensionType

  override def getPossibleCreatures(creatureType: EnumCreatureType, localPos: BlockPos): util.List[Biome.SpawnListEntry] =
    Option(vanillaGenerator.getPossibleCreatures(creatureType, localPos)).getOrElse(ImmutableList.of())
}

case class SolidLayer(filler: IBlockState, biome: Biome, realStartCubeY: Int, height: Int) extends Layer
