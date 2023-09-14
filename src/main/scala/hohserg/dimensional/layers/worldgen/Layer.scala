package hohserg.dimensional.layers.worldgen

import hohserg.dimensional.layers.DimensionLayersPreset.DimensionLayerSpec
import hohserg.dimensional.layers.legacy.fake.world.FakeWorld
import net.minecraft.block.state.IBlockState
import net.minecraft.world._
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.IChunkGenerator
import net.minecraft.world.storage.WorldInfo

trait Layer {
  def startCubeY: Int

  def height: Int
}

class VanillaLayer(spec: DimensionLayerSpec, val startCubeY: Int) extends Layer {
  private val provider: WorldProvider = spec.dimensionType.createDimension()
  new FakeWorld(provider, new WorldInfo(new WorldSettings(0, GameType.CREATIVE, true, false, WorldType.DEFAULT), "fake"), true)
  val vanillaGenerator: IChunkGenerator = provider.createChunkGenerator()
  val proxyWorld: World = null
  var lastChunk: Chunk = _
  var optimizationHack: Boolean = false
  var biomes: Array[Biome] = _

  override def height: Int = 16
}

case class SolidLayer(filler: IBlockState, startCubeY: Int, height: Int) extends Layer
