package hohserg.dimensional.layers.worldgen.proxy

import hohserg.dimensional.layers.worldgen.DimensionLayer
import io.github.opencubicchunks.cubicchunks.api.world.IMinMaxHeight
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.profiler.Profiler
import net.minecraft.tileentity.{TileEntity, TileEntityLockableLoot}
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.{Chunk, IChunkProvider}
import net.minecraft.world.storage.WorldInfo
import net.minecraft.world.storage.loot.LootTableManager
import net.minecraft.world.{World, WorldLens, WorldSettings}

import scala.collection.mutable

object ProxyWorld {
  def apply(original: World, layer: DimensionLayer): ProxyWorld = {
    val originalWorldInfo = original.getWorldInfo
    val actualWorldInfo = new WorldInfo(originalWorldInfo)
    actualWorldInfo.populateFromWorldSettings(
      new WorldSettings(
        layer.spec.seedOverride.getOrElse(original.getSeed),
        originalWorldInfo.getGameType,
        originalWorldInfo.isMapFeaturesEnabled,
        originalWorldInfo.isHardcoreModeEnabled,
        layer.spec.worldType
      ).setGeneratorOptions(layer.spec.worldTypePreset)
    )
    new ProxyWorld(original, layer, actualWorldInfo)
  }
}


class ProxyWorld private(original: World, val layer: DimensionLayer, actualWorldInfo: WorldInfo)
  extends BaseWorldServer(
    new FakeSaveHandler(actualWorldInfo),
    actualWorldInfo,
    layer.spec.dimensionType.createDimension(),
    new Profiler
  ) with FakeCubicWorld with IMinMaxHeight {

  provider.setWorld(this)

  override def createChunkProvider(): IChunkProvider = new ProxyChunkProvider(this, original, layer)

  chunkProvider = createChunkProvider()

  lootTable = new LootTableManager(null)

  initCapabilities()

  override def getSeed: Long = worldInfo.getSeed

  override def getMinHeight: Int = layer.startBlockY

  override def getMaxHeight: Int = layer.endBlockY + 1

  override def isOutsideBuildHeight(pos: BlockPos): Boolean = {
    pos match {
      case pos: ShiftedBlockPos =>
        !pos.isInLayer
      case _ =>
        super.isOutsideBuildHeight(pos)
    }
  }

  override def setBlockState(pos: BlockPos, newState: IBlockState, flags: Int): Boolean = {
    val shiftedPos = layer.shift(pos)
    getTileEntity(shiftedPos) match {
      case tile: TileEntityLockableLoot =>
        tile.setLootTable(null, 0)
      case _ =>
    }
    original.setBlockState(shiftedPos, newState, flags)
  }

  override def setBlockState(pos: BlockPos, state: IBlockState): Boolean = original.setBlockState(layer.shift(pos), state)

  override def isChunkLoaded(x: Int, z: Int, allowEmpty: Boolean): Boolean = WorldLens.isChunkLoaded(original, x, z, allowEmpty)

  override def isBlockLoaded(pos: BlockPos): Boolean = super.isBlockLoaded(pos)

  override def getBlockState(pos: BlockPos): IBlockState = original.getBlockState(layer.shift(pos))

  override def getTileEntity(pos: BlockPos): TileEntity = original.getTileEntity(layer.shift(pos))

  override def setTileEntity(pos: BlockPos, tileEntityIn: TileEntity): Unit = {
    tileEntityIn.setPos(layer.shift(tileEntityIn.getPos))
    original.setTileEntity(layer.shift(pos), tileEntityIn)
  }

  override def isAirBlock(pos: BlockPos): Boolean = original.isAirBlock(layer.shift(pos))

  override def markAndNotifyBlock(pos: BlockPos, chunk: Chunk, iblockstate: IBlockState, newState: IBlockState, flags: Int): Unit =
    original.markAndNotifyBlock(layer.shift(pos), chunk, iblockstate, newState, flags)

  override def destroyBlock(pos: BlockPos, dropBlock: Boolean): Boolean =
    original.destroyBlock(layer.shift(pos), dropBlock)

  override def notifyBlockUpdate(pos: BlockPos, oldState: IBlockState, newState: IBlockState, flags: Int): Unit =
    original.notifyBlockUpdate(layer.shift(pos), oldState, newState, flags)

  override def notifyNeighborsOfStateChange(pos: BlockPos, blockType: Block, updateObservers: Boolean): Unit =
    original.notifyNeighborsOfStateChange(layer.shift(pos), blockType, updateObservers)

  override def neighborChanged(pos: BlockPos, blockIn: Block, fromPos: BlockPos): Unit =
    original.neighborChanged(layer.shift(pos), blockIn, fromPos)

  override def observedNeighborChanged(pos: BlockPos, changedBlock: Block, changedBlockPos: BlockPos): Unit =
    original.observedNeighborChanged(layer.shift(pos), changedBlock, changedBlockPos)

  override def isBlockNormalCube(pos: BlockPos, _default: Boolean): Boolean =
    original.isBlockNormalCube(layer.shift(pos), _default)

  override def isSideSolid(pos: BlockPos, side: EnumFacing, _default: Boolean): Boolean =
    original.isSideSolid(layer.shift(pos), side, _default)

  override def getBlockLightOpacity(pos: BlockPos): Int =
    original.getBlockLightOpacity(layer.shift(pos))

  private val heightCache = new mutable.HashMap[(Int, Int), Int]

  override def getHeight(x: Int, z: Int): Int =
    heightCache.getOrElseUpdate(x -> z,
      (layer.endBlockY to layer.startBlockY by -1).dropWhile(y => original.isAirBlock(new BlockPos(x, y, z))).headOption
        .map(_ - layer.startBlockY)
        .getOrElse(0)
    )

  override def getBiome(pos: BlockPos): Biome = {
    val r = original.getBiome(layer.shift(pos).clamp)
    if (r == null)
      println("bruh biome null")
    r
  }

  override def getBiomeForCoordsBody(pos: BlockPos): Biome =
    original.getBiomeForCoordsBody(layer.shift(pos).clamp)

  override def canSeeSky(pos: BlockPos): Boolean =
    original.canSeeSky(layer.shift(pos))

  override def canBlockSeeSky(pos: BlockPos): Boolean =
    original.canBlockSeeSky(layer.shift(pos))

  override def getChunksLowestHorizon(x: Int, z: Int): Int =
    original.getChunksLowestHorizon(x, z)

  override def spawnEntity(entityIn: Entity): Boolean = {
    entityIn.posY += layer.startBlockY
    entityIn.world = original
    //original.spawnEntity(entityIn)
    false
  }
}