package hohserg.dimensional.layers

import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiFakeCreateWorld
import hohserg.dimensional.layers.preset.{DimensionalLayersPreset, Serialization}
import hohserg.dimensional.layers.sided.CommonLogic
import logictechcorp.netherex.world.WorldProviderNetherEx
import net.minecraft.client.Minecraft
import net.minecraft.world.DimensionType
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.Mod.{EventBusSubscriber, EventHandler}
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent, FMLServerStoppedEvent}
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.{Loader, Mod, Optional, SidedProxy}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.apache.commons.io.FileUtils

import java.io.File

@Mod(modid = Main.modid, name = Main.name, modLanguage = "scala")
@EventBusSubscriber
object Main {
  final val modid = "dimensional_layers"
  final val name = "DimensionalLayers"

  //-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./dump/oom.hprof
  //-Dlegacy.debugClassLoading=true -Dlegacy.debugClassLoadingSave=true

  @SidedProxy(clientSide = "hohserg.dimensional.layers.sided.ClientLogic", serverSide = "hohserg.dimensional.layers.sided.ServerLogic")
  var sided: CommonLogic = null

  final val netherexModid = "netherex"

  lazy val netherexPresent = Loader.isModLoaded(netherexModid)

  @EventHandler
  def preInit(e: FMLPreInitializationEvent): Unit = {
  }

  @EventHandler
  def init(e: FMLInitializationEvent): Unit = {
    sided.init(e)
    println(DimensionalLayersWorldType)
  }

  @SideOnly(Side.CLIENT)
  @EventHandler
  def fixClientLagByEarlyInit(e: FMLPostInitializationEvent): Unit = {
    println(
      DimensionalLayersWorldType.getName,
      DimensionalLayersPreset.mixedPresetTop,
      Serialization
    )
    new GuiSetupDimensionalLayersPreset(new GuiFakeCreateWorld(null, null, ""))
      .setWorldAndResolution(Minecraft.getMinecraft, Minecraft.getMinecraft.displayWidth, Minecraft.getMinecraft.displayHeight)
  }

  @EventHandler
  def serverStopped(e: FMLServerStoppedEvent): Unit = {
    println("serverStopped")
  }

  @Optional.Method(modid = netherexModid)
  @EventHandler
  def registerNetherExDimType(e: FMLInitializationEvent): Unit = {
    DimensionType.register("NetherEx", "_nether", -1, classOf[WorldProviderNetherEx], false)
  }
}
