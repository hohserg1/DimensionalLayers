package hohserg.dimensional.layers.worldgen.proxy.client

import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.profiler.Profiler
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.storage.{SaveHandlerMP, WorldInfo}
import net.minecraft.world.{EnumDifficulty, World, WorldProvider}

class BaseWorldClient(netHandler: NetHandlerPlayClient, info: WorldInfo, worldProvider: WorldProvider, difficulty: EnumDifficulty, profilerIn: Profiler)
  extends World(new SaveHandlerMP, info, worldProvider, profilerIn, true) {
  getWorldInfo.setDifficulty(difficulty)

  override def createChunkProvider(): IChunkProvider = ???

  override def isChunkLoaded(x: Int, z: Int, allowEmpty: Boolean): Boolean = ???
}
