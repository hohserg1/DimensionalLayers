package hohserg.dimensional.layers.worldgen.proxy.client

import net.minecraft.block.state.IBlockState
import net.minecraft.client.multiplayer.{ChunkProviderClient, WorldClient}
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.crash.{CrashReport, CrashReportCategory}
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.Packet
import net.minecraft.profiler.Profiler
import net.minecraft.scoreboard.Scoreboard
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{SoundCategory, SoundEvent}
import net.minecraft.world.chunk.{Chunk, IChunkProvider}
import net.minecraft.world.storage.WorldInfo
import net.minecraft.world.{DimensionType, EnumDifficulty, WorldSettings}

import java.util.Random

class BaseWorldClient(netHandler: NetHandlerPlayClient, info: WorldInfo, dimensionType: DimensionType, difficulty: EnumDifficulty, profilerIn: Profiler)
  extends WorldClient(netHandler, new WorldSettings(info), dimensionType.getId, difficulty, profilerIn) {
  override def tick(): Unit = {}

  override def createChunkProvider(): IChunkProvider = ???

  override def isChunkLoaded(x: Int, z: Int, allowEmpty: Boolean): Boolean = ???

  override def refreshVisibleChunks(): Unit = ???

  override def updateBlocks(): Unit = ???

  override def doPreChunk(chunkX: Int, chunkZ: Int, loadChunk: Boolean): Unit = ???

  override def spawnEntity(entityIn: Entity): Boolean = ???

  override def removeEntity(p_72900_1_ : Entity): Unit = ???

  override def onEntityAdded(p_72923_1_ : Entity): Unit = ???

  override def onEntityRemoved(p_72847_1_ : Entity): Unit = ???

  override def addEntityToWorld(p_73027_1_ : Int, p_73027_2_ : Entity): Unit = ???

  override def getEntityByID(id: Int): Entity = ???

  override def invalidateBlockReceiveRegion(p_73031_1_ : Int, p_73031_2_ : Int, p_73031_3_ : Int, p_73031_4_ : Int, p_73031_5_ : Int, p_73031_6_ : Int): Unit = ???

  override def removeEntityFromWorld(p_73028_1_ : Int): Entity = ???

  override def invalidateRegionAndSetBlock(p_180503_1_ : BlockPos, p_180503_2_ : IBlockState): Boolean = ???

  override def sendQuittingDisconnectingPacket(): Unit = ???

  override def updateWeather(): Unit = ???

  override def playMoodSoundAndCheckLight(p_147467_1_ : Int, p_147467_2_ : Int, p_147467_3_ : Chunk): Unit = ???

  override def doVoidFogParticles(p_73029_1_ : Int, p_73029_2_ : Int, p_73029_3_ : Int): Unit = ???

  override def showBarrierParticles(p_184153_1_ : Int, p_184153_2_ : Int, p_184153_3_ : Int, p_184153_4_ : Int, p_184153_5_ : Random, p_184153_6_ : Boolean, p_184153_7_ : BlockPos.MutableBlockPos): Unit = ???

  override def removeAllEntities(): Unit = ???

  override def addWorldInfoToCrashReport(p_72914_1_ : CrashReport): CrashReportCategory = ???

  override def playSound(p_184148_1_ : EntityPlayer, p_184148_2_ : Double, p_184148_4_ : Double, p_184148_6_ : Double, p_184148_8_ : SoundEvent, p_184148_9_ : SoundCategory, p_184148_10_ : Float, p_184148_11_ : Float): Unit = ???

  override def playSound(p_184156_1_ : BlockPos, p_184156_2_ : SoundEvent, p_184156_3_ : SoundCategory, p_184156_4_ : Float, p_184156_5_ : Float, p_184156_6_ : Boolean): Unit = ???

  override def playSound(p_184134_1_ : Double, p_184134_3_ : Double, p_184134_5_ : Double, p_184134_7_ : SoundEvent, p_184134_8_ : SoundCategory, p_184134_9_ : Float, p_184134_10_ : Float, p_184134_11_ : Boolean): Unit = ???

  override def makeFireworks(p_92088_1_ : Double, p_92088_3_ : Double, p_92088_5_ : Double, p_92088_7_ : Double, p_92088_9_ : Double, p_92088_11_ : Double, p_92088_13_ : NBTTagCompound): Unit = ???

  override def sendPacketToServer(p_184135_1_ : Packet[?]): Unit = ???

  override def setWorldScoreboard(p_96443_1_ : Scoreboard): Unit = ???

  override def setWorldTime(p_72877_1_ : Long): Unit = ???

  override def getChunkProvider: ChunkProviderClient = ???
}
