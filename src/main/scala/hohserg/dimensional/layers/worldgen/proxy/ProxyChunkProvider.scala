package hohserg.dimensional.layers.worldgen.proxy

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import hohserg.dimensional.layers.worldgen.DimensionLayer
import net.minecraft.world.World
import net.minecraft.world.chunk.{Chunk, IChunkProvider}

class ProxyChunkProvider(original: World, layer: DimensionLayer) extends IChunkProvider {

  val proxyChunkCache: LoadingCache[Chunk, ProxyChunk] =
    CacheBuilder.newBuilder()
      .weakKeys()
      .maximumSize(128 * 128)
      .build(new CacheLoader[Chunk, ProxyChunk] {
        override def load(key: Chunk): ProxyChunk = new ProxyChunk(key, layer)
      })

  override def getLoadedChunk(x: Int, z: Int): Chunk = Option(original.getChunkProvider.getLoadedChunk(x, z)).map(proxyChunkCache.get).orNull

  override def provideChunk(x: Int, z: Int): Chunk = proxyChunkCache.get(original.getChunkProvider.provideChunk(x, z))

  override def tick(): Boolean = false

  override def makeString(): String = ""

  override def isChunkGeneratedAt(x: Int, z: Int): Boolean = original.getChunkProvider.isChunkGeneratedAt(x, z)
}
