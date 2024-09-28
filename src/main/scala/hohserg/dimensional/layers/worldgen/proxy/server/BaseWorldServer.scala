package hohserg.dimensional.layers.worldgen.proxy.server

import com.sun.istack.internal.Nullable
import net.minecraft.advancements.{AdvancementManager, FunctionManager}
import net.minecraft.block.Block
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.entity.{Entity, EntityTracker, EnumCreatureType}
import net.minecraft.profiler.Profiler
import net.minecraft.server.management.PlayerChunkMap
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumParticleTypes, IProgressUpdate}
import net.minecraft.world._
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.storage.{ISaveHandler, WorldInfo}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import java.io.File
import java.util

abstract class BaseWorldServer(saveHandlerIn: ISaveHandler, info: WorldInfo, providerIn: WorldProvider, profilerIn: Profiler)
  extends World(saveHandlerIn, info, providerIn, profilerIn, false) {

  override def init(): World = this

  override def tick(): Unit = {}

  @Nullable
  def getSpawnListEntryForTypeAt(creatureType: EnumCreatureType, pos: BlockPos): Biome.SpawnListEntry = ???

  def canCreatureTypeSpawnHere(creatureType: EnumCreatureType, spawnListEntry: Biome.SpawnListEntry, pos: BlockPos): Boolean = ???

  override def updateAllPlayersSleepingFlag(): Unit = {}

  protected def wakeAllPlayers(): Unit = ???

  def areAllPlayersAsleep: Boolean = ???

  @SideOnly(Side.CLIENT)
  override def setInitialSpawnLocation(): Unit = ???

  protected def playerCheckLight(): Unit = ???

  override protected def updateBlocks(): Unit = {}

  protected def adjustPosToNearbyEntity(pos: BlockPos): BlockPos = ???

  override def isBlockTickPending(pos: BlockPos, blockType: Block): Boolean = false

  override def isUpdateScheduled(pos: BlockPos, blk: Block): Boolean = true

  override def scheduleUpdate(pos: BlockPos, blockIn: Block, delay: Int): Unit = {}

  override def updateBlockTick(pos: BlockPos, blockIn: Block, delay: Int, priority: Int): Unit = {}

  override def scheduleBlockUpdate(pos: BlockPos, blockIn: Block, delay: Int, priority: Int): Unit = {}

  override def updateEntities(): Unit = {}

  override def tickPlayers(): Unit = {}

  def resetUpdateEntityTick(): Unit = {}

  override def tickUpdates(runAllPending: Boolean): Boolean = false

  @Nullable
  override def getPendingBlockUpdates(chunkIn: Chunk, remove: Boolean): util.List[NextTickListEntry] = null

  @Nullable
  override def getPendingBlockUpdates(structureBB: StructureBoundingBox, remove: Boolean): util.List[NextTickListEntry] = null

  override def updateEntityWithOptionalForce(entityIn: Entity, forceUpdate: Boolean): Unit = ???

  override def canMineBlockBody(player: EntityPlayer, pos: BlockPos): Boolean = true

  override def initialize(settings: WorldSettings): Unit = ???

  @throws[MinecraftException]
  def saveAllChunks(all: Boolean, @Nullable progressCallback: IProgressUpdate): Unit = ???

  def flushToDisk(): Unit = ???

  @throws[MinecraftException]
  protected def saveLevel(): Unit = ???

  override def loadEntities(entityCollection: util.Collection[Entity]): Unit = {}

  override def onEntityAdded(entityIn: Entity): Unit = ???

  override def onEntityRemoved(entityIn: Entity): Unit = ???

  override def addWeatherEffect(entityIn: Entity): Boolean = ???

  override def setEntityState(entityIn: Entity, state: Byte): Unit = ???

  override def getChunkProvider = chunkProvider

  override def newExplosion(@Nullable entityIn: Entity, x: Double, y: Double, z: Double, strength: Float, causesFire: Boolean, damagesTerrain: Boolean): Explosion = ???

  override def addBlockEvent(pos: BlockPos, blockIn: Block, eventID: Int, eventParam: Int): Unit = {
    this.getBlockState(pos).onBlockEventReceived(this, pos, eventID, eventParam)
  }

  def flush(): Unit = ???

  override protected def updateWeather(): Unit = ???

  def getEntityTracker(): EntityTracker = ???

  def getPlayerChunkMap(): PlayerChunkMap = ???

  def getDefaultTeleporter(): Teleporter = ???

  def spawnParticle(particleType: EnumParticleTypes, longDistance: Boolean, xCoord: Double, yCoord: Double, zCoord: Double, numberOfParticles: Int, xOffset: Double, yOffset: Double, zOffset: Double, particleSpeed: Double, particleArguments: Int*): Unit = {}

  def spawnParticle(player: EntityPlayerMP, particle: EnumParticleTypes, longDistance: Boolean, x: Double, y: Double, z: Double, count: Int, xOffset: Double, yOffset: Double, zOffset: Double, speed: Double, arguments: Int*): Unit = {}

  @Nullable override def findNearestStructure(structureName: String, position: BlockPos, findUnexplored: Boolean): BlockPos = ???

  def getAdvancementManager(): AdvancementManager = ???

  def getFunctionManager(): FunctionManager = ???

  def getChunkSaveLocation(): File = ???

}
