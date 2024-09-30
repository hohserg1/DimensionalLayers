package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.CCWorld
import hohserg.dimensional.layers.data.layer.base.Layer
import hohserg.dimensional.layers.preset.DimensionalLayersPreset
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import net.minecraft.entity.Entity


class WorldData(val original: CCWorld) {
  val preset = DimensionalLayersPreset.fromJson(original.getWorldInfo.getGeneratorOptions)

  val layerAtCubeY: Map[Int, Layer] = preset.toLayerMap(preset.toLayerSeq(original), identity)

  def getLayerOf(entity: Entity): Option[Layer] = {
    val y = Coords.blockToCube((entity.posY + 0.5).toInt)
    layerAtCubeY.get(y)
  }
}