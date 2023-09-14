package hohserg.dimensional.layers.legacy

import hohserg.dimensional.layers.legacy.fake.world.FakeWorld
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Biomes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome

sealed trait Layer {
  def getChunkContentAt: (Int, Int) => (Int, Int, Int) => IBlockState

  def getBiomeAt: (Int, Int) => (Int, Int) => Biome


}

object Layer {

  case class FakeWorldLayer(world: FakeWorld) extends Layer {
    override val getChunkContentAt: (Int, Int) => (Int, Int, Int) => IBlockState =
      (x, z) => {
        val chunk = world.getChunk(x, z)
        (i, j, k) => chunk.getBlockState(i, j, k)
      }
    override val getBiomeAt: (Int, Int) => (Int, Int) => Biome = (chunkX, chunkZ) => {
      val chunk = world.getChunk(chunkX, chunkZ)
      (x, z) => chunk.getBiome(new BlockPos(x, 1, z), world.getBiomeProvider)
    }

  }

  case class SolidLayer(state: IBlockState, biome: Biome = Biomes.VOID) extends Layer {
    override val getChunkContentAt: (Int, Int) => (Int, Int, Int) => IBlockState = (_, _) => (_, _, _) => state

    override val getBiomeAt: (Int, Int) => (Int, Int) => Biome = (_, _) => (_, _) => biome
  }

}
