package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.{CCWorld, DimensionalLayersWorldType}

import scala.collection.mutable

trait LayerManager[SidedOriginalWorld <: CCWorld] {

  private val worldDataForRealDimension = new mutable.OpenHashMap[Int, WorldData]()

  def unload(world: SidedOriginalWorld): Unit = {
    worldDataForRealDimension -= world.provider.getDimension
  }

  def haveWorldLayers(world: SidedOriginalWorld): Boolean = {
    world.getWorldInfo.getTerrainType == DimensionalLayersWorldType && DimensionalLayersWorldType.hasCubicGeneratorForWorld(world)
  }

  def getWorldData(world: SidedOriginalWorld): Option[WorldData] =
    if (haveWorldLayers(world))
      Some(worldDataForRealDimension.getOrElseUpdate(world.provider.getDimension, new WorldData(world)))
    else
      None

}
