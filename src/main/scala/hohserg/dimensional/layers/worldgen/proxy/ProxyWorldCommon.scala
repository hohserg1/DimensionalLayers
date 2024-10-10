package hohserg.dimensional.layers.worldgen.proxy

import com.pg85.otg.util.helpers.ReflectionHelper
import hohserg.dimensional.layers.data.layer.base.DimensionalLayer
import hohserg.dimensional.layers.{CCWorld, Main}
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import io.github.opencubicchunks.cubicchunks.api.world.{ICube, ICubeProvider, ICubicWorld}
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.{Entity, EntityList}
import net.minecraft.tileentity.{TileEntity, TileEntityLockableLoot}
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.{Chunk, IChunkProvider}
import net.minecraft.world.storage.loot.LootTableManager
import net.minecraft.world.{DimensionType, World, WorldLens}

import scala.collection.mutable

trait ProxyWorldCommon {
  this: World with ICubicWorld =>

  type ProxyChunkProvider <: IChunkProvider with ICubeProvider

  def original: CCWorld

  def layer: DimensionalLayer

  def createProxyChunkProvider(): ProxyChunkProvider

  lazy val proxyChunkProvider: ProxyChunkProvider = createProxyChunkProvider()

  override def createChunkProvider(): IChunkProvider = proxyChunkProvider

  def initWorld(): Unit = {
    if (provider.getClass.getName == "com.pg85.otg.forge.dimensions.OTGWorldProvider") {
      ReflectionHelper.setValueInFieldOfType(provider, classOf[DimensionType], layer.dimensionType)
    }
    provider.setWorld(this)
    provider.setDimension(layer.dimensionType.getId)

    WorldLens.setChunkProvider(this, proxyChunkProvider)

    WorldLens.setLootTable(this, new LootTableManager(null))
  }

  def bounds = layer.bounds

  override def getSeed: Long = worldInfo.getSeed

  override def isCubicWorld: Boolean = layer.isCubic

  override def getCubeCache: ICubeProvider = proxyChunkProvider

  override def getCubeFromCubeCoords(cx: Int, cy: Int, cz: Int): ICube =
    proxyChunkProvider.getCube(cx, cy, cz)

  override def getCubeFromBlockCoords(pos: BlockPos): ICube =
    proxyChunkProvider.getCube(Coords.blockToCube(pos.getX), Coords.blockToCube(pos.getY), Coords.blockToCube(pos.getZ))

  override def getMinHeight: Int = 0

  override def getMaxHeight: Int = 256

  override def isOutsideBuildHeight(pos: BlockPos): Boolean = {
    pos match {
      case pos: ShiftedBlockPos =>
        !pos.isInLayer
      case _ =>
        pos.getY < 0 || pos.getY >= 256
    }
  }

  override def setBlockState(pos: BlockPos, newState: IBlockState, flags: Int): Boolean = {
    val shiftedPos = layer.bounds.shift(pos)
    getTileEntity(shiftedPos) match {
      case tile: TileEntityLockableLoot =>
        tile.setLootTable(null, 0)
      case _ =>
    }
    original.setBlockState(shiftedPos, newState, flags)
  }

  override def setBlockState(pos: BlockPos, state: IBlockState): Boolean = original.setBlockState(layer.bounds.shift(pos), state)

  override def isChunkLoaded(x: Int, z: Int, allowEmpty: Boolean): Boolean = WorldLens.isChunkLoaded(original, x, z, allowEmpty)

  override def getBlockState(pos: BlockPos): IBlockState = original.getBlockState(layer.bounds.shift(pos))

  override def getTileEntity(pos: BlockPos): TileEntity = original.getTileEntity(layer.bounds.shift(pos))

  override def setTileEntity(pos: BlockPos, tileEntityIn: TileEntity): Unit = {
    tileEntityIn.setPos(layer.bounds.shift(tileEntityIn.getPos))
    original.setTileEntity(layer.bounds.shift(pos), tileEntityIn)
  }

  override def isAirBlock(pos: BlockPos): Boolean = original.isAirBlock(layer.bounds.shift(pos))

  override def markAndNotifyBlock(pos: BlockPos, chunk: Chunk, iblockstate: IBlockState, newState: IBlockState, flags: Int): Unit =
    original.markAndNotifyBlock(layer.bounds.shift(pos), chunk, iblockstate, newState, flags)

  override def destroyBlock(pos: BlockPos, dropBlock: Boolean): Boolean =
    original.destroyBlock(layer.bounds.shift(pos), dropBlock)

  override def notifyBlockUpdate(pos: BlockPos, oldState: IBlockState, newState: IBlockState, flags: Int): Unit =
    original.notifyBlockUpdate(layer.bounds.shift(pos), oldState, newState, flags)

  override def notifyNeighborsOfStateChange(pos: BlockPos, blockType: Block, updateObservers: Boolean): Unit =
    original.notifyNeighborsOfStateChange(layer.bounds.shift(pos), blockType, updateObservers)

  override def neighborChanged(pos: BlockPos, blockIn: Block, fromPos: BlockPos): Unit =
    original.neighborChanged(layer.bounds.shift(pos), blockIn, fromPos)

  override def observedNeighborChanged(pos: BlockPos, changedBlock: Block, changedBlockPos: BlockPos): Unit =
    original.observedNeighborChanged(layer.bounds.shift(pos), changedBlock, changedBlockPos)

  override def isBlockNormalCube(pos: BlockPos, _default: Boolean): Boolean =
    original.isBlockNormalCube(layer.bounds.shift(pos), _default)

  override def isSideSolid(pos: BlockPos, side: EnumFacing, _default: Boolean): Boolean =
    original.isSideSolid(layer.bounds.shift(pos), side, _default)

  override def getBlockLightOpacity(pos: BlockPos): Int =
    original.getBlockLightOpacity(layer.bounds.shift(pos))

  private val heightCache = new mutable.HashMap[(Int, Int), Int]

  override def getHeight(x: Int, z: Int): Int =
    heightCache.getOrElseUpdate(x -> z,
      (layer.bounds.realEndBlockY to layer.bounds.realStartBlockY by -1).dropWhile(y => original.getBlockLightOpacity(new BlockPos(x, y, z)) == 0).headOption
        .map(_ - layer.bounds.realStartBlockY + 1)
        .getOrElse(0)
    )

  override def getBiome(pos: BlockPos): Biome = {
    val r = original.getBiome(layer.bounds.shift(pos).clamp)
    if (r == null)
      Main.sided.printError("bruh biome null", "Context(seed=" + original.getSeed + "layer=" + layer.spec + ")", new NullPointerException(""))
    r
  }

  override def getBiomeForCoordsBody(pos: BlockPos): Biome =
    original.getBiomeForCoordsBody(layer.bounds.shift(pos).clamp)

  override def canSeeSky(pos: BlockPos): Boolean =
    original.canSeeSky(layer.bounds.shift(pos))

  override def canBlockSeeSky(pos: BlockPos): Boolean =
    original.canBlockSeeSky(layer.bounds.shift(pos))

  override def getChunksLowestHorizon(x: Int, z: Int): Int =
    original.getChunksLowestHorizon(x, z)

  override def spawnEntity(entityIn: Entity): Boolean = {
    entityIn.setLocationAndAngles(entityIn.posX, layer.bounds.shiftBlockY(entityIn.posY), entityIn.posZ, entityIn.rotationYaw, entityIn.rotationPitch)
    entityIn.world = original
    val newEntity = EntityList.newEntity(entityIn.getClass, original)
    newEntity.deserializeNBT(entityIn.serializeNBT())
    original.spawnEntity(newEntity)
    false
  }

  override def setEntityState(entityIn: Entity, state: Byte): Unit = original.setEntityState(entityIn, state)

}
