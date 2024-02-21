package hohserg.dimensional.layers

import hohserg.dimensional.layers.preset.DimensionalLayersPreset
import hohserg.dimensional.layers.worldgen.Layer
import hohserg.dimensional.layers.worldgen.proxy.client.ProxyWorldClient
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import net.minecraft.entity.Entity

import scala.collection.mutable

object DimensionalLayersManager {
  class WorldData(val world: CCWorld) {
    val preset = DimensionalLayersPreset(world.getWorldInfo.getGeneratorOptions)
    val layerAtCubeY: Map[Int, Layer] = preset.toLayerMap(preset.toLayerSeq(world))

    def getLayerOf(entity: Entity): Option[Layer] = {
      val y = Coords.blockToCube((entity.posY + 0.5D).toInt)
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

  def getClientWorld: ProxyWorldClient = ???
}
