package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.{CCWorld, clamp}
import hohserg.dimensional.layers.data.layer.base.{DimensionalLayer, Layer}
import hohserg.dimensional.layers.preset.SingleDimensionPreset
import io.github.opencubicchunks.cubicchunks.api.util.{Coords, IntRange}
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

  val spawnLayer = layers.apply(clamp(layers.size - preset.spawnLayerReversIndex - 1, 0, layers.size))
  val minSpawnBlockY = Coords.cubeToMinBlock(spawnLayer._1._1)
  val maxSpawnBlockY = Coords.cubeToMaxBlock(spawnLayer._1._2)

  def getLayerOf(entity: Entity): Option[Layer] = {
    getLayerAt((entity.posY + 0.5).toInt)
  }

  def getLayerAt(blockY: Int): Option[Layer] = {
    layerAtCubeY.get(Coords.blockToCube(blockY))
  }

  def getDimensionalLayerAt(blockY: Int): Option[DimensionalLayer] =
    getLayerAt(blockY).collect { case l: DimensionalLayer => l }
}