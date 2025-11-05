package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.*
import hohserg.dimensional.layers.preset.{DimensionalLayersPreset, SingleDimensionPreset}
import hohserg.dimensional.layers.worldgen.proxy.ProxyWorldCommon
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager

import scala.collection.mutable

object LayerManagerServer extends LayerManager {

  private var world: World = null
  private var preset: Option[DimensionalLayersPreset] = None

  private val worldDataForRealDimension = new mutable.HashMap[Int, Option[WorldData]]()

  def getPreset(proofOfLoaded: World): Option[DimensionalLayersPreset] = {
    val mainWorld = DimensionManager.getWorld(0)
    if (!(world eq mainWorld)) {
      world = mainWorld
      if (mainWorld.getWorldInfo.getTerrainType == DimensionalLayersWorldType)
        preset = Some(DimensionalLayersPreset.fromJson(mainWorld.getWorldInfo.getGeneratorOptions))
      else
        preset = None
      worldDataForRealDimension.clear()
    }
    preset
  }

  def haveWorldLayers(world: World): Boolean = {
    getPreset(world).exists(_.realDimensionToLayers.contains(world.provider.getDimension))
  }

  def getWorldData(world: World): Option[WorldData] = {
    if (world.isInstanceOf[ProxyWorldCommon])
      None
    else {
      val dimensionId = world.provider.getDimension
      val maybePreset = getPreset(world)
      worldDataForRealDimension.getOrElseUpdate(
        dimensionId,
        maybePreset.flatMap(_.realDimensionToLayers.get(dimensionId))
                   .map(new WorldData(world.asInstanceOf[CCWorld], _: SingleDimensionPreset))
      )
    }
  }
}