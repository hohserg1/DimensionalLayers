package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.data.LayerManagerServer.preset
import hohserg.dimensional.layers.lens.DimensionManagerLens
import hohserg.dimensional.layers.preset.{DimensionalLayersPreset, SingleDimensionPreset}
import hohserg.dimensional.layers.{CCWorld, CCWorldServer, DimensionalLayersWorldType, Main}
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld
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

  private val worldDataForRealDimension = new mutable.HashMap[Int, WorldData]()

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

  @SubscribeEvent
  def unloadWorld(e: WorldEvent.Unload): Unit = {
    val world = e.getWorld
    if (!world.isRemote) {
      worldDataForRealDimension -= world.provider.getDimension
      if (world.provider.getDimension == 0) {
        preset = None
        checked = false
        worldDataForRealDimension.clear()
      }
    }
  }

  def haveWorldLayers(world: World): Boolean = {
    getPreset(world).exists(_.realDimensionToLayers.contains(world.provider.getDimension))
  }

  def getWorldData(world: World): Option[WorldData] = {
    val dimensionId = world.provider.getDimension
    getPreset(world).flatMap(_.realDimensionToLayers.get(dimensionId))
                    .map(new WorldData(world.asInstanceOf[CCWorld], _: SingleDimensionPreset))
                    .foreach(worldDataForRealDimension += dimensionId -> _)
    worldDataForRealDimension.get(dimensionId)
  }
}