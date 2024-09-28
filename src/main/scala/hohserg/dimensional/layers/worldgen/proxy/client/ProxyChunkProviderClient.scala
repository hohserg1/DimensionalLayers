package hohserg.dimensional.layers.worldgen.proxy.client

import hohserg.dimensional.layers.CCWorldClient
import hohserg.dimensional.layers.data.layer.base.DimensionalLayer
import hohserg.dimensional.layers.worldgen.proxy.ProxyChunkProviderCommon
import io.github.opencubicchunks.cubicchunks.api.world.ICubeProvider
import net.minecraft.client.multiplayer.ChunkProviderClient

case class ProxyChunkProviderClient(proxy: ProxyWorldClient, original: CCWorldClient, layer: DimensionalLayer)
  extends ChunkProviderClient(proxy)
    with ICubeProvider
    with ProxyChunkProviderCommon {
}
