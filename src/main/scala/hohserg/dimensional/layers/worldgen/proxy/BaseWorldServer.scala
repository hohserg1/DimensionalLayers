package hohserg.dimensional.layers.worldgen.proxy

import com.sun.istack.internal.Nullable
import net.minecraft.entity.EntityTracker
import net.minecraft.profiler.Profiler
import net.minecraft.server.management.PlayerChunkMap
import net.minecraft.util.IProgressUpdate
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.storage.{ISaveHandler, WorldInfo}
import net.minecraft.world.{MinecraftException, Teleporter, World, WorldProvider}

abstract class BaseWorldServer(saveHandlerIn: ISaveHandler, info: WorldInfo, providerIn: WorldProvider, profilerIn: Profiler)
  extends World(saveHandlerIn, info, providerIn, profilerIn, false) {

  override def getChunkProvider: IChunkProvider = chunkProvider

  def flushToDisk(): Unit = {

  }

  @throws[MinecraftException]
  def saveAllChunks(all: Boolean, @Nullable progressCallback: IProgressUpdate): Unit = {
  }

  def getEntityTracker: EntityTracker = ???

  def getPlayerChunkMap: PlayerChunkMap = ???

  def getDefaultTeleporter: Teleporter = ???

}
