package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.data.layer.base.Layer
import hohserg.dimensional.layers.preset.DimensionalLayersPreset
import hohserg.dimensional.layers.{CCWorld, DimensionalLayersWorldType}
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import net.minecraft.entity.Entity

import scala.collection.mutable

object LayerManager {

  class WorldData(val original: CCWorld) {
    val preset = DimensionalLayersPreset(original.getWorldInfo.getGeneratorOptions)

    private val seq = preset.toLayerSeq(original)

    val layerAtCubeY: Map[Int, Layer] = preset.toLayerMap(seq, identity)

    def getLayerOf(entity: Entity): Option[Layer] = {
      val y = Coords.blockToCube((entity.posY + 0.5).toInt)
      layerAtCubeY.get(y)
    }
  }

  private val worldDataForRealDimension = new mutable.OpenHashMap[Int, WorldData]()

  def haveWorldLayers(world: CCWorld) = {
    world.getWorldInfo.getTerrainType == DimensionalLayersWorldType && DimensionalLayersWorldType.hasCubicGeneratorForWorld(world)
  }

  def initRealDimension(world: CCWorld): Unit = {
    if (haveWorldLayers(world))
      worldDataForRealDimension += world.provider.getDimension -> new WorldData(world)
  }

  def getWorldData(world: CCWorld): Option[WorldData] =
    if (haveWorldLayers(world))
      worldDataForRealDimension.get(world.provider.getDimension)
    else
      None
}
