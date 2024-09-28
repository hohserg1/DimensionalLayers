package hohserg.dimensional.layers.data.layer.cubic_world_type

import hohserg.dimensional.layers.data.layer.base.{DimensionalGenerator, DimensionalLayer, DimensionalLayerBounds}
import hohserg.dimensional.layers.preset.CubicWorldTypeLayerSpec
import hohserg.dimensional.layers.{CCWorld, CCWorldServer}
import net.minecraft.world.DimensionType

case class CubicWorldTypeLayer(_realStartCubeY: Int, spec: CubicWorldTypeLayerSpec, originalWorld: CCWorld) extends DimensionalLayer {
  override type Spec = CubicWorldTypeLayerSpec
  override type G = DimensionalGenerator

  override def bounds: DimensionalLayerBounds = new DimensionalLayerBounds {
    override val realStartCubeY: Int = _realStartCubeY
    override val (virtualStartCubeY, virtualEndCubeY) = spec.rangeCube(originalWorld)
    override val cubeHeight: Int = virtualEndCubeY - virtualStartCubeY + 1
  }

  override def isCubic: Boolean = true

  override def dimensionType: DimensionType = spec.dimensionType1

  override def createGenerator(original: CCWorldServer): DimensionalGenerator = new CubicWorldTypeGenerator(original, this)

}
