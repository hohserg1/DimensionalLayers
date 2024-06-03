package hohserg.dimensional.layers.data.layer.base

import hohserg.dimensional.layers.preset.LayerSpec
import hohserg.dimensional.layers.worldgen.proxy.client.ProxyWorldClient
import hohserg.dimensional.layers.{CCWorld, CCWorldClient, CCWorldServer}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

trait Layer {
  type Spec <: LayerSpec
  type Bounds <: LayerBounds

  def bounds: Bounds

  def spec: Spec

  def originalWorld: CCWorld

  protected def createGenerator(original: CCWorldServer): Generator

  @SideOnly(Side.CLIENT)
  protected def createClientProxyWorld(original: CCWorldClient): ProxyWorldClient

  lazy val generator: Generator = originalWorld match {
    case serverWorld: CCWorldServer => createGenerator(serverWorld)
  }

  @SideOnly(Side.CLIENT)
  lazy val clientProxyWorld: ProxyWorldClient = originalWorld match {
    case clientWorld: CCWorldClient => createClientProxyWorld(clientWorld)
  }

}
