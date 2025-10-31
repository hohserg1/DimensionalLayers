package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.CCWorld
import hohserg.dimensional.layers.data.layer.base.{DimensionalLayer, Layer}
import hohserg.dimensional.layers.preset.SingleDimensionPreset
import io.github.opencubicchunks.cubicchunks.api.util.{Coords, IntRange}
import net.minecraft.entity.Entity


class WorldData(val original: CCWorld, val preset: SingleDimensionPreset) {
  val layers: Seq[(IntRange, Layer)] = preset.toLayerSeq(original)

  val layerAtCubeY: LayerMap = LayerMap(layers)

  type DimensionId = Int

  lazy val dimensionRelatedLayers: Map[DimensionId, Seq[DimensionalLayer]] = layers.map(_._2).collect { case l: DimensionalLayer => l }.groupBy(_.dimensionType.getId)

  val minCubeY: Int = layers.minBy(_._1.getMin)._1.getMin
  val maxCubeY: Int = layers.maxBy(_._1.getMax)._1.getMax
  val minBlockY = minCubeY << 4
  val maxBlockY = (maxCubeY << 4) + 15

  def getLayerOf(entity: Entity): Option[Layer] = {
    getLayerAt((entity.posY + 0.5).toInt)
  }

  def getLayerAt(blockY: Int): Option[Layer] = {
    layerAtCubeY.get(Coords.blockToCube(blockY))
  }

  def getDimensionalLayerAt(blockY: Int): Option[DimensionalLayer] =
    getLayerAt(blockY).collect { case l: DimensionalLayer => l }
}