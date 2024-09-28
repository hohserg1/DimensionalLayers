package hohserg.dimensional.layers.data.layer.base

import hohserg.dimensional.layers.preset.LayerSpec
import hohserg.dimensional.layers.worldgen.proxy.client.ProxyWorldClient
import hohserg.dimensional.layers.{CCWorld, CCWorldClient, CCWorldServer}
import net.minecraft.world.DimensionType
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

trait Layer {
  type Spec <: LayerSpec
  type Bounds <: LayerBounds

  type G <: Generator

  def bounds: Bounds

  def spec: Spec

  def originalWorld: CCWorld

  protected def createGenerator(original: CCWorldServer): G

  lazy val generator: G = originalWorld match {
    case serverWorld: CCWorldServer => createGenerator(serverWorld)
  }

}

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