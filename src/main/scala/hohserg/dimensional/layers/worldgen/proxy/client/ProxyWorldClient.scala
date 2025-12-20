package hohserg.dimensional.layers.worldgen.proxy.client

import hohserg.dimensional.layers.CCWorldClient
import hohserg.dimensional.layers.data.layer.base.DimensionalLayer
import hohserg.dimensional.layers.worldgen.proxy.ProxyWorldCommon
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.profiler.Profiler

class ProxyWorldClient(val original: CCWorldClient, val layer: DimensionalLayer)
  extends BaseWorldClient(Minecraft.getMinecraft.getConnection, original.getWorldInfo, layer.dimensionId, original.getDifficulty, new Profiler)
    with FakeCubicWorldClient
    with ProxyWorldCommon {

  override type ProxyChunkProvider = ProxyChunkProviderClient

  override def createProxyChunkProvider(): ProxyChunkProviderClient = ProxyChunkProviderClient(this, original, layer)

  initWorld()

  override def getEntityByID(id: Int): Entity = original.getEntityByID(id)
}
