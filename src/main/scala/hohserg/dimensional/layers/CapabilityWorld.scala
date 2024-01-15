package hohserg.dimensional.layers

import hohserg.dimensional.layers.CapabilityWorld.capa
import hohserg.dimensional.layers.preset.DimensionalLayersPreset
import hohserg.dimensional.layers.worldgen.Layer
import io.github.opencubicchunks.cubicchunks.api.util.IntRange
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject, ICapabilityProvider}

class CapabilityWorld(world: CCWorld) extends ICapabilityProvider {
  val preset = DimensionalLayersPreset(world.getWorldInfo.getGeneratorOptions)

  private val seq: Seq[(IntRange, Layer)] = preset.toLayerSeq(world)

  val layerAtCubeY: Map[Int, Layer] = preset.toLayerMap(seq)

  //val layerByDimensionType: Map[DimensionType, Seq[Layer]] = preset

  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean =
    capability == capa

  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T =
    capa.cast(if (hasCapability(capa, facing))
      this
    else
      null
    )
}

object CapabilityWorld {
  @CapabilityInject(classOf[CapabilityWorld])
  var capa: Capability[CapabilityWorld] = _

  def apply(world: World): CapabilityWorld = world.getCapability(capa, null)

}
