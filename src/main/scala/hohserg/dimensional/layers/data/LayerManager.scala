package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.{CCWorld, DimensionalLayersWorldType}
import net.minecraft.world.World
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import scala.collection.mutable

trait LayerManager[SidedOriginalWorld <: CCWorld] {

  private val worldDataForRealDimension = new mutable.HashMap[Int, WorldData]()

  def haveWorldLayers(world: SidedOriginalWorld): Boolean = {
    world.getWorldInfo.getTerrainType == DimensionalLayersWorldType && DimensionalLayersWorldType.hasCubicGeneratorForWorld(world)
  }

  def getWorldData(world: World): Option[WorldData] =
    if (haveWorldLayers(world.asInstanceOf[SidedOriginalWorld]))
      Some(worldDataForRealDimension.getOrElseUpdate(world.provider.getDimension, createWorldData(world.asInstanceOf[SidedOriginalWorld])))
    else
      None

  protected def createWorldData(world: SidedOriginalWorld): WorldData = new WorldData(world)

  @SubscribeEvent
  def unloadWorld(e: WorldEvent.Unload): Unit = {
    e.getWorld match {
      case w: SidedOriginalWorld =>
        worldDataForRealDimension -= w.provider.getDimension
      case _ =>
    }
  }

}
