package hohserg.dimensional.layers.data.layer.base

import hohserg.dimensional.layers.CCWorldClient
import hohserg.dimensional.layers.worldgen.proxy.client.ProxyWorldClient
import net.minecraft.world.DimensionType
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

trait DimensionalLayer extends Layer {
  override type Bounds = DimensionalLayerBounds
  override type G <: DimensionalGenerator

  def dimensionType: DimensionType

  def isCubic: Boolean

  @SideOnly(Side.CLIENT)
  protected def createClientProxyWorld(original: CCWorldClient): ProxyWorldClient = ProxyWorldClient(original, this)

  @SideOnly(Side.CLIENT)
  lazy val clientProxyWorld: ProxyWorldClient = originalWorld match {
    case clientWorld: CCWorldClient => createClientProxyWorld(clientWorld)
  }
}
