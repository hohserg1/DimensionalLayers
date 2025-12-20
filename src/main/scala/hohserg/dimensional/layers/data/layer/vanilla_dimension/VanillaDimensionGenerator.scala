package hohserg.dimensional.layers.data.layer.vanilla_dimension

import hohserg.dimensional.layers.CCWorldServer
import hohserg.dimensional.layers.data.layer.base.VanillaDimensionGeneratorBase
import net.minecraft.world.WorldType

class VanillaDimensionGenerator(original: CCWorldServer, layer: VanillaDimensionLayer)
  extends VanillaDimensionGeneratorBase[VanillaDimensionLayer](original, layer, layer.spec.offsets) {

  override def seedOverride: Option[Long] = layer.spec.seedOverride

  override def worldType: WorldType = layer.spec.worldType

  override def worldTypePreset: String = layer.spec.worldTypePreset
}
