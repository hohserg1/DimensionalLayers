package hohserg.dimensional.layers.data.layer.solid

import hohserg.dimensional.layers.data.layer.base.{Generator, Layer, LayerBounds}
import hohserg.dimensional.layers.preset.SolidLayerSpec
import hohserg.dimensional.layers.worldgen.proxy.client.ProxyWorldClient
import hohserg.dimensional.layers.{CCWorld, CCWorldClient, CCWorldServer}

case class SolidLayer(_realStartCubeY: Int, spec: SolidLayerSpec, originalWorld: CCWorld) extends Layer {
  override type Spec = SolidLayerSpec
  override type Bounds = LayerBounds

  override def bounds: LayerBounds = new LayerBounds {
    override val realStartCubeY: Int = _realStartCubeY

    override val cubeHeight: Int = spec.height
  }

  override def createGenerator(original: CCWorldServer): Generator = new SolidGenerator(this)

  override def createClientProxyWorld(original: CCWorldClient): ProxyWorldClient = ???
}
