package hohserg.dimensional.layers

import hohserg.dimensional.layers.compatibility.event.CompatEventsHandler
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiFakeCreateWorld
import hohserg.dimensional.layers.preset.{DimensionalLayersPreset, Serialization}
import hohserg.dimensional.layers.sided.CommonLogic
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiCreateWorld
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.Mod.{EventBusSubscriber, EventHandler}
import net.minecraftforge.fml.common.event.{FMLPostInitializationEvent, FMLPreInitializationEvent, FMLServerStoppedEvent}
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.{Loader, Mod, SidedProxy}
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
  var sided: CommonLogic = _

  lazy val otgPresent = Loader.isModLoaded("openterraingenerator")

  @EventHandler
  def init(e: FMLPreInitializationEvent): Unit = {
    CompatEventsHandler.init()
  }

  @SideOnly(Side.CLIENT)
  @EventHandler
  def fixClientLagByEarlyInit(e: FMLPostInitializationEvent): Unit = {
    println(
      DimensionalLayersWorldType.getName,
      DimensionalLayersPreset.mixedPresetTop,
      Serialization.gson
    )
    new GuiSetupDimensionalLayersPreset(new GuiFakeCreateWorld(null, ""))
      .setWorldAndResolution(Minecraft.getMinecraft, Minecraft.getMinecraft.displayWidth, Minecraft.getMinecraft.displayHeight)
  }


  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  def onGuiOpen(e: GuiOpenEvent): Unit = {
    GuiFakeCreateWorld.replaceGuiByParent(e)
  }

  @EventHandler
  def serverStopped(e: FMLServerStoppedEvent): Unit = {
    println("serverStopped")
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  def onGuiCreateWorld(event: GuiOpenEvent): Unit = {
    if (Configuration.worldTypeByDefault)
      event.getGui match {
        case guiCreateWorld: GuiCreateWorld =>
          if (guiCreateWorld.worldSeed.isEmpty)
            guiCreateWorld.selectedIndex = DimensionalLayersWorldType.getId
        case _ =>
      }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  def unmarkDLWorldFromOTG(e: WorldEvent.Save): Unit = {
    if (e.getWorld.getWorldType == DimensionalLayersWorldType) {
      FileUtils.deleteDirectory(new File(e.getWorld.getSaveHandler.getWorldDirectory, "OpenTerrainGenerator"))
    }
  }
}
