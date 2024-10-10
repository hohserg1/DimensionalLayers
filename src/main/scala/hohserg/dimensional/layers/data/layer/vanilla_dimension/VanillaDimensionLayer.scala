package hohserg.dimensional.layers.data.layer.vanilla_dimension

import hohserg.dimensional.layers.data.layer.base.{DimensionalLayer, DimensionalLayerBounds}
import hohserg.dimensional.layers.preset.DimensionLayerSpec
import hohserg.dimensional.layers.{CCWorld, CCWorldServer}
import net.minecraft.world.DimensionType

case class VanillaDimensionLayer(_realStartCubeY: Int, spec: DimensionLayerSpec, originalWorld: CCWorld) extends DimensionalLayer {
  override type Spec = DimensionLayerSpec
  override type G = VanillaDimensionGenerator

  override val bounds: DimensionalLayerBounds = new DimensionalLayerBounds {
    override val realStartCubeY: Int = _realStartCubeY
    override val cubeHeight: Int = spec.height
    override val virtualStartCubeY: Int = spec.bottomOffset
    override val virtualEndCubeY: Int = 16 - spec.topOffset - 1
  }

  override def isCubic: Boolean = false

  override def dimensionType: DimensionType = spec.dimensionType

  override def createGenerator(original: CCWorldServer): VanillaDimensionGenerator = new VanillaDimensionGenerator(original, this)

}
