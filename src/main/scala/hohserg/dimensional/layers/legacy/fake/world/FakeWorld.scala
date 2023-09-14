package hohserg.dimensional.layers.legacy.fake.world

import net.minecraft.block.state.IBlockState
import net.minecraft.profiler.Profiler
import net.minecraft.tileentity.TileEntityLockableLoot
import net.minecraft.util.math.BlockPos
import net.minecraft.world._
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.storage.WorldInfo
import net.minecraft.world.storage.loot.LootTableManager

class FakeWorld(originalProvider: WorldProvider,
                info: WorldInfo,
                hasSkyLight: Boolean,
                capacity: Int = 200 * 200)
  extends World(
    new FakeSaveHandler(info),
    info,
    new FakeWorldProvider(originalProvider, hasSkyLight),
    new Profiler,
    false
  ) with FakeCubicWorld {

  originalProvider.setWorld(this)
  provider.setWorld(this)
  chunkProvider = createChunkProvider()

  lootTable = new LootTableManager(null)

  override def setBlockState(pos: BlockPos, newState: IBlockState, flags: Int): Boolean = {
    getTileEntity(pos) match {
      case tile: TileEntityLockableLoot =>
        tile.setLootTable(null, 0)
      case _ =>
    }
    super.setBlockState(pos, newState, flags)
  }

  override def createChunkProvider(): IChunkProvider = new FakeChunkProvider(originalProvider.createChunkGenerator(), capacity)

  override def isChunkLoaded(x: Int, z: Int, allowEmpty: Boolean): Boolean = chunkProvider.isChunkGeneratedAt(x, z)
}

object FakeWorld {
  def apply(dimensionType: DimensionType, seed: Long, enableMapFeatures: Boolean, hasSkyLight: Boolean): FakeWorld =
    new FakeWorld(
      dimensionType.createDimension(),
      new WorldInfo(new WorldSettings(seed, GameType.CREATIVE, enableMapFeatures, false, WorldType.DEFAULT), "world"),
      hasSkyLight
    )
}
