package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.worldgen.proxy.client.BaseWorldClient
import hohserg.dimensional.layers.worldgen.proxy.server.BaseWorldServer
import hohserg.dimensional.layers.{CCWorld, DimensionalLayersWorldType}
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import scala.collection.mutable

trait LayerManager[SidedOriginalWorld <: CCWorld] {

  private val worldDataForRealDimension = new mutable.OpenHashMap[Int, WorldData]()

  def haveWorldLayers(world: SidedOriginalWorld): Boolean = {
    world.getWorldInfo.getTerrainType == DimensionalLayersWorldType && DimensionalLayersWorldType.hasCubicGeneratorForWorld(world)
  }

  def getWorldData(world: SidedOriginalWorld): Option[WorldData] =
    if (haveWorldLayers(world))
      Some(worldDataForRealDimension.getOrElseUpdate(world.provider.getDimension, createWorldData(world)))
    else
      None

  protected def createWorldData(world: SidedOriginalWorld): WorldData = new WorldData(world)

  @SubscribeEvent
  def unloadWorld(e: WorldEvent.Unload): Unit = {
    e.getWorld match {
      case _: BaseWorldServer => println("wtf: unloadWorld BaseWorldServer", e)
      case _: BaseWorldClient => println("wtf: unloadWorld BaseWorldServer", e)

      case w: SidedOriginalWorld =>
        worldDataForRealDimension -= w.provider.getDimension
      case _ =>
    }
  }

}
