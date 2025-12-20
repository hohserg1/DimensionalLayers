package hohserg.dimensional.layers.data.layer.vanilla_dimension

import hohserg.dimensional.layers.data.layer.base.{DimensionalLayer, DimensionalLayerBounds, VanillaDimensionLayerBase}
import hohserg.dimensional.layers.preset.spec.DimensionLayerSpec
import hohserg.dimensional.layers.{CCWorld, CCWorldServer}
import net.minecraft.world.DimensionType

case class VanillaDimensionLayer(_realStartCubeY: Int, spec: DimensionLayerSpec, originalWorld: CCWorld) 
  extends VanillaDimensionLayerBase(_realStartCubeY, originalWorld, spec.offsets) {
  override type Spec = DimensionLayerSpec
  override type G = VanillaDimensionGenerator

  override def dimensionType: DimensionType = spec.dimensionType

  override def createGenerator(original: CCWorldServer): VanillaDimensionGenerator = new VanillaDimensionGenerator(original, this)

}
