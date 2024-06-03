package hohserg.dimensional.layers.data.layer.cubic_world_type

import hohserg.dimensional.layers.data.layer.base.{DimensionalLayer, DimensionalLayerBounds, Generator}
import hohserg.dimensional.layers.preset.CubicWorldTypeLayerSpec
import hohserg.dimensional.layers.worldgen.proxy.client.ProxyWorldClient
import hohserg.dimensional.layers.{CCWorld, CCWorldClient, CCWorldServer}

case class CubicWorldTypeLayer(_realStartCubeY: Int, spec: CubicWorldTypeLayerSpec, originalWorld: CCWorld) extends DimensionalLayer {
  override type Spec = CubicWorldTypeLayerSpec

  override def bounds: DimensionalLayerBounds = new DimensionalLayerBounds {
    override val realStartCubeY: Int = _realStartCubeY
    override val (virtualStartCubeY, virtualEndCubeY) = spec.rangeCube(originalWorld)
    override val cubeHeight: Int = virtualEndCubeY - virtualStartCubeY + 1
  }

  override def createGenerator(original: CCWorldServer): Generator = new CubicWorldTypeGenerator(original, this)

  override def createClientProxyWorld(original: CCWorldClient): ProxyWorldClient = ???
}
