package hohserg.dimensional.layers.data.layer.vanilla_dimension

import hohserg.dimensional.layers.data.layer.base.{DimensionalLayer, DimensionalLayerBounds, Generator}
import hohserg.dimensional.layers.preset.DimensionLayerSpec
import hohserg.dimensional.layers.worldgen.proxy.client.ProxyWorldClient
import hohserg.dimensional.layers.{CCWorld, CCWorldClient, CCWorldServer}

case class VanillaDimensionLayer(_realStartCubeY: Int, spec: DimensionLayerSpec, originalWorld: CCWorld) extends DimensionalLayer {
  override type Spec = DimensionLayerSpec

  override def bounds: DimensionalLayerBounds = new DimensionalLayerBounds {
    override val realStartCubeY: Int = _realStartCubeY
    override val cubeHeight: Int = spec.height
    override val virtualStartCubeY: Int = spec.bottomOffset
    override val virtualEndCubeY: Int = 16 - spec.topOffset - 1
  }

  override def createGenerator(original: CCWorldServer): Generator = new VanillaDimensionGenerator(original, this)

  override def createClientProxyWorld(original: CCWorldClient): ProxyWorldClient = ???
}
