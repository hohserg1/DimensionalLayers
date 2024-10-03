package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.CCWorld
import hohserg.dimensional.layers.data.layer.base.{DimensionalLayer, Layer}
import hohserg.dimensional.layers.preset.DimensionalLayersPreset
import io.github.opencubicchunks.cubicchunks.api.util.{Coords, IntRange}
import net.minecraft.entity.Entity


class WorldData(val original: CCWorld) {
  val preset = DimensionalLayersPreset.fromJson(original.getWorldInfo.getGeneratorOptions)

  private val seq: Seq[(IntRange, Layer)] = preset.toLayerSeq(original)

  val layerAtCubeY: LayerMap = LayerMap(seq)

  val dimensionRelatedLayers: Map[Int, Seq[DimensionalLayer]] = seq.map(_._2).collect { case l: DimensionalLayer => l }.groupBy(_.dimensionType.getId)

  def getLayerOf(entity: Entity): Option[Layer] = {
    val y = Coords.blockToCube((entity.posY + 0.5).toInt)
    layerAtCubeY.get(y)
  }
}