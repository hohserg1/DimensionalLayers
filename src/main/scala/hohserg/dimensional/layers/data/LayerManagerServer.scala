package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.*
import hohserg.dimensional.layers.preset.{DimensionalLayersPreset, SingleDimensionPreset}
import hohserg.dimensional.layers.worldgen.proxy.ProxyWorldCommon
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import scala.collection.mutable

@EventBusSubscriber
object LayerManagerServer extends LayerManager {

  private var checked = false
  private var preset: Option[DimensionalLayersPreset] = None

  private val worldDataForRealDimension = new mutable.HashMap[Int, Option[WorldData]]()

  def getPreset(proofOfLoaded: World): Option[DimensionalLayersPreset] = {
    if (!checked) {
      checked = true
      val mainWorld = DimensionManager.getWorld(0)
      if (mainWorld.getWorldInfo.getTerrainType == DimensionalLayersWorldType) {
        preset = Some(DimensionalLayersPreset.fromJson(mainWorld.getWorldInfo.getGeneratorOptions))
      }
    }
    preset
  }

  private def clear(): Unit = {
    preset = None
    checked = false
    worldDataForRealDimension.clear()
  }

  @SubscribeEvent
  def unloadWorld(e: WorldEvent.Unload): Unit = {
    val world = e.getWorld
    if (!world.isRemote) {
      if (!world.isInstanceOf[ProxyWorldCommon]) {
        worldDataForRealDimension -= world.provider.getDimension
        if (world.provider.getDimension == 0) {
          clear()
        }
      }
    }
  }

  @SubscribeEvent
  def bcWorldUnloadEventMayNotFire(e: WorldEvent.Load): Unit = {
    val world = e.getWorld
    if (!world.isRemote) {
      if (!world.isInstanceOf[ProxyWorldCommon]) {
        if (world.provider.getDimension == 0) {
          clear()
          getPreset(world)
        }
      }
    }
  }

  def haveWorldLayers(world: World): Boolean = {
    getPreset(world).exists(_.realDimensionToLayers.contains(world.provider.getDimension))
  }

  def getWorldData(world: World): Option[WorldData] = {
    if (world.isInstanceOf[ProxyWorldCommon])
      None
    else {
      val dimensionId = world.provider.getDimension
      worldDataForRealDimension.getOrElseUpdate(
        dimensionId,
        getPreset(world).flatMap(_.realDimensionToLayers.get(dimensionId))
                        .map(new WorldData(world.asInstanceOf[CCWorld], _: SingleDimensionPreset))
      )
    }
  }
}