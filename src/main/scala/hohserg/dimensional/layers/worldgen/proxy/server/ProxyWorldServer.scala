package hohserg.dimensional.layers.worldgen.proxy.server

import hohserg.dimensional.layers.data.layer.base.{DimensionalLayer, Generator}
import hohserg.dimensional.layers.data.layer.cubic_world_type.{CubicWorldTypeGenerator, CubicWorldTypeLayer}
import hohserg.dimensional.layers.data.layer.vanilla_dimension.VanillaDimensionLayer
import hohserg.dimensional.layers.worldgen.proxy.{ProxyWorldCommon, ShiftedBlockPos}
import hohserg.dimensional.layers.{CCWorldServer, Main}
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import io.github.opencubicchunks.cubicchunks.api.world.{ICube, ICubeProviderServer}
import io.github.opencubicchunks.cubicchunks.api.worldgen.ICubeGenerator
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.{Entity, EntityList, EnumCreatureType}
import net.minecraft.profiler.Profiler
import net.minecraft.tileentity.{TileEntity, TileEntityLockableLoot}
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, WeightedRandom}
import net.minecraft.world._
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.storage.WorldInfo
import net.minecraft.world.storage.loot.LootTableManager

import java.util
import scala.collection.mutable

object ProxyWorldServer {
  def apply(original: CCWorldServer, layer: VanillaDimensionLayer, generator: Generator): ProxyWorldServer = {
    new ProxyWorldServer(
      original,
      layer,
      generator,
      layer.spec.dimensionType,
      createLayerWorldInfo(original, layer.spec.seedOverride, layer.spec.worldType, layer.spec.worldTypePreset),
      isCubicWorld = false
    )
  }

  def apply(original: CCWorldServer, layer: CubicWorldTypeLayer, generator: CubicWorldTypeGenerator): ProxyWorldServer = {
    new ProxyWorldServer(
      original,
      layer,
      generator,
      layer.spec.dimensionType1,
      createLayerWorldInfo(original, layer.spec.seedOverride, layer.spec.cubicWorldType, layer.spec.worldTypePreset),
      isCubicWorld = true
    )
  }

  def createLayerWorldInfo(original: World, seedOverride: Option[Long], worldType: WorldType, worldTypePreset: String): WorldInfo = {
    val originalWorldInfo = original.getWorldInfo
    val actualWorldInfo = new WorldInfo(originalWorldInfo)
    actualWorldInfo.populateFromWorldSettings(
      new WorldSettings(
        seedOverride.getOrElse(original.getSeed),
        originalWorldInfo.getGameType,
        originalWorldInfo.isMapFeaturesEnabled,
        originalWorldInfo.isHardcoreModeEnabled,
        worldType
      ).setGeneratorOptions(worldTypePreset)
    )
    actualWorldInfo
  }
}


class ProxyWorldServer private(original: CCWorldServer, val layer: DimensionalLayer, generator: Generator, dimensionType: DimensionType, actualWorldInfo: WorldInfo, override val isCubicWorld: Boolean)
  extends BaseWorldServer(
    new FakeSaveHandler(actualWorldInfo),
    actualWorldInfo,
    dimensionType.createDimension(),
    new Profiler
  ) with ProxyWorldCommon with FakeCubicWorldServer {

  provider.setWorld(this)
  provider.setDimension(dimensionType.getId)

  override def createChunkProvider(): ProxyChunkProviderServer = new ProxyChunkProviderServer(this, original, layer)

  val proxyChunkProvider: ProxyChunkProviderServer = createChunkProvider()

  chunkProvider = proxyChunkProvider

  lootTable = new LootTableManager(null)

  initCapabilities()

  def bounds = layer.bounds

  override def getSeed: Long = worldInfo.getSeed

  override def getMinHeight: Int = layer.bounds.virtualStartBlockY

  override def getMaxHeight: Int = layer.bounds.virtualEndBlockY + 1

  override def isOutsideBuildHeight(pos: BlockPos): Boolean = {
    pos match {
      case pos: ShiftedBlockPos =>
        !pos.isInLayer
      case _ =>
        super.isOutsideBuildHeight(pos)
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

  override def isBlockLoaded(pos: BlockPos): Boolean = super.isBlockLoaded(pos)

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
      (layer.bounds.realEndBlockY to layer.bounds.realStartBlockY by -1).dropWhile(y => original.isAirBlock(new BlockPos(x, y, z))).headOption
        .map(_ - layer.bounds.realStartBlockY)
        .getOrElse(0)
    )

  override def getBiome(pos: BlockPos): Biome = {
    val r = original.getBiome(layer.bounds.shift(pos).clamp)
    if (r == null)
      Main.sided.printError("bruh biome null", new NullPointerException(""))
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

  override def getSpawnListEntryForTypeAt(creatureType: EnumCreatureType, pos: BlockPos): Biome.SpawnListEntry = {
    val list = getPossibleCreatures(creatureType, pos)
    if (!list.isEmpty)
      WeightedRandom.getRandomItem(this.rand, list)
    else
      null
  }

  override def canCreatureTypeSpawnHere(creatureType: EnumCreatureType, spawnListEntry: Biome.SpawnListEntry, pos: BlockPos): Boolean =
    getPossibleCreatures(creatureType, pos).contains(spawnListEntry)

  private def getPossibleCreatures(creatureType: EnumCreatureType, pos: BlockPos): util.List[Biome.SpawnListEntry] =
    generator.getPossibleCreatures(creatureType, ShiftedBlockPos.unshift(pos))

  override def getCubeFromCubeCoords(cx: Int, cy: Int, cz: Int): ICube =
    proxyChunkProvider.getCube(cx, cy, cz)

  override def getCubeFromBlockCoords(pos: BlockPos): ICube =
    proxyChunkProvider.getCube(Coords.blockToCube(pos.getX), Coords.blockToCube(pos.getY), Coords.blockToCube(pos.getZ))

  override def getCubeCache: ICubeProviderServer = proxyChunkProvider

  override def getCubeGenerator: ICubeGenerator =
    generator match {
      case generator: CubicWorldTypeGenerator => generator.generator
      case _ => null
    }

  override def setEntityState(entityIn: Entity, state: Byte): Unit = original.setEntityState(entityIn, state)
}