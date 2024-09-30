package hohserg.dimensional.layers

import hohserg.dimensional.layers.compatibility.event.CompatEventsHandler
import hohserg.dimensional.layers.data.{LayerManagerClient, LayerManagerServer}
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiFakeCreateWorld
import hohserg.dimensional.layers.preset.{CubicWorldTypeLayerSpec, DimensionLayerSpec, DimensionalLayersPreset, Serialization}
import hohserg.dimensional.layers.sided.CommonLogic
import hohserg.dimensional.layers.worldgen.proxy.client.BaseWorldClient
import hohserg.dimensional.layers.worldgen.proxy.server.BaseWorldServer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiCreateWorld
import net.minecraft.client.resources.I18n
import net.minecraft.world.DimensionType
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.Mod.{EventBusSubscriber, EventHandler}
import net.minecraftforge.fml.common.event.{FMLPostInitializationEvent, FMLPreInitializationEvent, FMLServerStoppedEvent}
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import java.util.Random

@Mod(modid = Main.modid, name = Main.name, modLanguage = "scala")
@EventBusSubscriber
object Main {
  final val modid = "dimensional_layers"
  final val name = "DimensionalLayers"

  //-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./dump/oom.hprof
  //-Dlegacy.debugClassLoading=true -Dlegacy.debugClassLoadingSave=true

  @SidedProxy(clientSide = "hohserg.dimensional.layers.sided.ClientLogic", serverSide = "hohserg.dimensional.layers.sided.ServerLogic")
  var sided: CommonLogic = _

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
  @SubscribeEvent(priority = EventPriority.LOWEST)
  def onGuiCreateWorld(event: ActionPerformedEvent.Pre): Unit =
    if (Configuration.worldTypeByDefault)
      event.getGui match {
        case guiCreateWorld: GuiCreateWorld =>
          if (guiCreateWorld.worldSeed.isEmpty) {
            if (event.getButton.displayString == I18n.format("selectWorld.moreWorldOptions") ||
              event.getButton.displayString == I18n.format("selectWorld.create")) {
              guiCreateWorld.selectedIndex = DimensionalLayersWorldType.getId

            } else if (event.getButton.displayString == I18n.format("gui.done")) {
              guiCreateWorld.worldSeed = new Random().nextLong.toString
              guiCreateWorld.worldSeedField.setText(guiCreateWorld.worldSeed);
            }
          }

        case _ =>
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

  @SubscribeEvent
  def changeLayerInsteadOfDimension(e: EntityTravelToDimensionEvent): Unit = {
    if (true)
      return

    val entity = e.getEntity
    val current = entity.dimension
    val target = e.getDimension
    val targetDimType = DimensionType.getById(target)

    if (DimensionalLayersWorldType.hasCubicGeneratorForWorld(entity.world)) {
      val preset = DimensionalLayersPreset.fromJson(entity.world.getWorldInfo.getGeneratorOptions)
      preset.layers.find {
        case spec: DimensionLayerSpec if spec.dimensionType == targetDimType =>
          true
        case spec: CubicWorldTypeLayerSpec if spec.dimensionType1 == targetDimType =>
          true
        case _ =>
          false
      } foreach { layer =>

      }
    }
  }

  @SubscribeEvent
  def unloadWorld(e: WorldEvent.Unload): Unit = {
    e.getWorld match {
      case _: BaseWorldServer => println("wtf: unloadWorld BaseWorldServer", e)
      case _: BaseWorldClient => println("wtf: unloadWorld BaseWorldServer", e)

      case w: CCWorldClient => LayerManagerClient.unload(w)
      case w: CCWorldServer => LayerManagerServer.unload(w)
    }
  }
}
