package hohserg.dimensional.layers.data.layer.cubic_world_type

import hohserg.dimensional.layers.data.layer.base.{DimensionalLayer, DimensionalLayerBounds, Generator}
import hohserg.dimensional.layers.preset.spec.CubicWorldTypeLayerSpec
import hohserg.dimensional.layers.{CCWorld, CCWorldServer}
import net.minecraft.world.DimensionType

case class CubicWorldTypeLayer(_realStartCubeY: Int, spec: CubicWorldTypeLayerSpec, originalWorld: CCWorld) extends DimensionalLayer {
  override type Spec = CubicWorldTypeLayerSpec
  override type G = CubicWorldTypeGenerator

  override val bounds: DimensionalLayerBounds = new DimensionalLayerBounds {
    override val realStartCubeY: Int = _realStartCubeY
    override val (virtualStartCubeY, virtualEndCubeY) = spec.minCubeY -> spec.maxCubeY
    override val cubeHeight: Int = virtualEndCubeY - virtualStartCubeY + 1
  }

  override def isCubic: Boolean = true

  override def dimensionType: DimensionType = spec.dimensionType1

  override protected def createGenerator(original: CCWorldServer): G = new CubicWorldTypeGenerator(original, this)

}
