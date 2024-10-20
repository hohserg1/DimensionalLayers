package hohserg.dimensional.layers.data.layer.solid

import hohserg.dimensional.layers.data.layer.base.{Layer, LayerBounds}
import hohserg.dimensional.layers.preset.spec.SolidLayerSpec
import hohserg.dimensional.layers.{CCWorld, CCWorldServer}

case class SolidLayer(_realStartCubeY: Int, spec: SolidLayerSpec, originalWorld: CCWorld) extends Layer {
  override type Spec = SolidLayerSpec
  override type Bounds = LayerBounds
  override type G = SolidGenerator

  override def bounds: LayerBounds = new LayerBounds {
    override val realStartCubeY: Int = _realStartCubeY

    override val cubeHeight: Int = spec.height
  }

  override def createGenerator(original: CCWorldServer): SolidGenerator = new SolidGenerator(this)
}
