package hohserg.dimensional.layers.worldgen.proxy

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import hohserg.dimensional.layers.CCWorld
import hohserg.dimensional.layers.data.layer.base.DimensionalLayer
import io.github.opencubicchunks.cubicchunks.api.util.CubePos
import io.github.opencubicchunks.cubicchunks.api.world.{ICube, ICubeProvider}
import net.minecraft.world.chunk.{Chunk, IChunkProvider}

trait ProxyChunkProviderCommon {
  this: IChunkProvider & ICubeProvider =>

  def proxy: CCWorld & ProxyWorldCommon

  def original: CCWorld

  def layer: DimensionalLayer

  val proxyChunkCache: LoadingCache[Chunk, ProxyChunk] =
    CacheBuilder.newBuilder()
      .weakKeys()
      .maximumSize(128 * 128)
      .build(new CacheLoader[Chunk, ProxyChunk] {
        override def load(key: Chunk): ProxyChunk = new ProxyChunk(proxy, key, layer.bounds)
      })

  val proxyCubeCache: LoadingCache[ICube, ProxyCube] =
    if (proxy.isCubicWorld)
      CacheBuilder.newBuilder()
        .weakKeys()
        .maximumSize(128 * 128)
        .build(new CacheLoader[ICube, ProxyCube] {
          override def load(key: ICube): ProxyCube = new ProxyCube(key, layer.bounds, proxy)
        })
    else
      null

  override def getLoadedChunk(cx: Int, cz: Int): Chunk = Option(original.getChunkProvider.getLoadedChunk(cx, cz)).map(proxyChunkCache.get).orNull

  override def provideChunk(cx: Int, cz: Int): Chunk = proxyChunkCache.get(original.getChunkProvider.provideChunk(cx, cz))

  override def tick(): Boolean = false

  override def makeString(): String = "ProxyChunkProvider"

  override def isChunkGeneratedAt(x: Int, z: Int): Boolean = original.getChunkProvider.isChunkGeneratedAt(x, z)

  //cubic

  def filterNonCubic[A](f: => A): A =
    if (proxy.isCubicWorld)
      f
    else
      null.asInstanceOf[A]

  override def getLoadedCube(cx: Int, cy: Int, cz: Int): ICube =
    filterNonCubic(Option(original.getCubeCache.getLoadedCube(cx, cy, cz)).map(proxyCubeCache.get).orNull)

  override def getLoadedCube(cubePos: CubePos): ICube =
    getLoadedCube(cubePos.getX, cubePos.getY, cubePos.getZ)

  override def getCube(cx: Int, cy: Int, cz: Int): ICube =
    filterNonCubic(proxyCubeCache.get(original.getCubeCache.getCube(cx, cy, cz)))

  override def getCube(cubePos: CubePos): ICube =
    getCube(cubePos.getX, cubePos.getY, cubePos.getZ)

  override def getLoadedColumn(cx: Int, cz: Int): Chunk =
    getLoadedChunk(cx, cz)

  override def provideColumn(cx: Int, cz: Int): Chunk =
    provideChunk(cx, cz)

}
