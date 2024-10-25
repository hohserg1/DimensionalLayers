package hohserg.dimensional.layers.asm.dev.tweaks

import com.pg85.otg.OTG
import com.pg85.otg.configuration.biome.BiomeConfig
import com.pg85.otg.forge.events.server.ServerEventListener
import com.pg85.otg.forge.world.ForgeWorld
import com.pg85.otg.network.ConfigProvider
import com.pg85.otg.util.BiomeIds
import gloomyfolken.hooklib.api._
import net.minecraftforge.fml.common.event.FMLServerStartingEvent

@HookContainer
object OTGDebug {

  @Hook
  @OnBegin
  def createBiomeFor(forgeWorld: ForgeWorld, biomeConfig: BiomeConfig, biomeIds: BiomeIds, configProvider: ConfigProvider, isReload: Boolean): Unit = {
    println("createBiomeFor", forgeWorld.world, biomeConfig.getName)
  }

  @Hook
  @OnMethodCall(value = "loadCustomDimensionData", shift = Shift.BEFORE)
  def serverLoad(l: ServerEventListener, event: FMLServerStartingEvent): ReturnSolve[Void] = {
    if (OTG.getDimensionsConfig != null && OTG.getDimensionsConfig.Overworld.DimensionId != 0)
      ReturnSolve.yes(null)
    else
      ReturnSolve.no()
  }

}
