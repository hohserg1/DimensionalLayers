package hohserg.dimensional.layers.worldgen.proxy

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import hohserg.dimensional.layers.worldgen.DimensionLayer
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.ChunkProviderServer
import net.minecraft.world.{World, WorldServer}

class ProxyChunkProvider(proxy: ProxyWorld, original: World, layer: DimensionLayer)
  extends ChunkProviderServer(proxy.asInstanceOf[WorldServer], proxy.getSaveHandler.getChunkLoader(proxy.provider), layer.vanillaGenerator) {

  val proxyChunkCache: LoadingCache[Chunk, ProxyChunk] =
    CacheBuilder.newBuilder()
      .weakKeys()
      .maximumSize(128 * 128)
      .build(new CacheLoader[Chunk, ProxyChunk] {
        override def load(key: Chunk): ProxyChunk = new ProxyChunk(proxy, key, layer)
      })

  override def getLoadedChunk(x: Int, z: Int): Chunk = Option(original.getChunkProvider.getLoadedChunk(x, z)).map(proxyChunkCache.get).orNull

  override def provideChunk(x: Int, z: Int): Chunk = proxyChunkCache.get(original.getChunkProvider.provideChunk(x, z))

  override def tick(): Boolean = false

  override def makeString(): String = "ProxyChunkProvider"

  override def isChunkGeneratedAt(x: Int, z: Int): Boolean = original.getChunkProvider.isChunkGeneratedAt(x, z)

  override def loadChunk(x: Int, z: Int): Chunk = ???

  override def loadChunk(x: Int, z: Int, runnable: Runnable): Chunk = ???

  override def saveChunks(all: Boolean): Boolean = false

  override def flushToDisk(): Unit = ()

  override def canSave: Boolean = false

}
