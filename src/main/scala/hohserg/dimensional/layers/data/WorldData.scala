package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.data.layer.base.{DimensionalLayer, Layer}
import hohserg.dimensional.layers.preset.SingleDimensionPreset
import hohserg.dimensional.layers.{CCWorld, clamp}
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import net.minecraft.entity.Entity


class WorldData(val original: CCWorld, val preset: SingleDimensionPreset) {
  val layers: Seq[((Int, Int), Layer)] = preset.toLayerSeq(original)

  val layerAtCubeY: LayerMap = LayerMap(layers)

  type DimensionId = Int

  lazy val dimensionRelatedLayers: Map[DimensionId, Seq[DimensionalLayer]] = layers.map(_._2).collect { case l: DimensionalLayer => l }.groupBy(_.dimensionType.getId)

  val minCubeY: Int = layers.map { case ((min, _), _) => min }.min
  val maxCubeY: Int = layers.map { case ((_, max), _) => max }.max
  val minBlockY = Coords.cubeToMinBlock(minCubeY)
  val maxBlockY = Coords.cubeToMaxBlock(maxCubeY)

  val (minSpawnBlockY, maxSpawnBlockY, spawnLayer) = {
    val ((cubeMinY, cubeMaxY), spawnLayer) = layers.apply(clamp(layers.size - preset.spawnLayerReversIndex - 1, 0, layers.size))
    (Coords.cubeToMinBlock(cubeMinY), Coords.cubeToMaxBlock(cubeMaxY), spawnLayer)
  }

  def getLayerOf(entity: Entity): Option[Layer] = {
    getLayerAt((entity.posY + 0.5).toInt)
  }

  def getLayerAt(blockY: Int): Option[Layer] = {
    layerAtCubeY.get(Coords.blockToCube(blockY))
  }

  def getDimensionalLayerAt(blockY: Int): Option[DimensionalLayer] =
    getLayerAt(blockY).collect { case l: DimensionalLayer => l }
}