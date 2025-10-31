package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.*
import hohserg.dimensional.layers.preset.DimensionalLayersPreset
import hohserg.dimensional.layers.worldgen.proxy.ProxyWorldCommon
import net.minecraft.world.World
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent
import net.minecraftforge.fml.relauncher.Side

@EventBusSubscriber(value = Array(Side.CLIENT))
object LayerManagerClient extends LayerManager {

  private var preset: Option[DimensionalLayersPreset] = None
  private var currentWorldLayersChecked = false
  private var currentWorldLayers: Option[WorldData] = None

  def onPresetPacket(preset: String): Unit = {
    this.preset = Some(DimensionalLayersPreset.fromJson(preset))
    currentWorldLayers = None
    currentWorldLayersChecked = false
  }

  def clear(): Unit = {
    preset = None
    currentWorldLayers = None
    currentWorldLayersChecked = false
  }

  override def haveWorldLayers(world: World): Boolean = getWorldData(world).isDefined

  override def getWorldData(world: World): Option[WorldData] = {
    if (world.isInstanceOf[ProxyWorldCommon])
      None
    else {
      if (!currentWorldLayersChecked) {
        currentWorldLayersChecked = true
        currentWorldLayers = preset.flatMap(_.realDimensionToLayers.get(world.provider.getDimension))
                                   .map(p => new WorldData(world.asInstanceOf[CCWorld], p))
      }
      currentWorldLayers
    }
  }

  @SubscribeEvent
  def unloadWorld(e: WorldEvent.Unload): Unit = {
    currentWorldLayers = None
    currentWorldLayersChecked = false
  }

  @SubscribeEvent
  def disconnect(e: ClientDisconnectionFromServerEvent): Unit = {
    clear()
  }
}
