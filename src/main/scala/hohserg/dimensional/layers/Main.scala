package hohserg.dimensional.layers

import net.minecraft.client.gui.GuiCreateWorld
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.{EventBusSubscriber, EventHandler}
import net.minecraftforge.fml.common.event.{FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@Mod(modid = Main.modid, name = "DimensionalLayers", modLanguage = "scala")
@EventBusSubscriber
object Main {
  final val modid = "dimensional_layers"

  @EventHandler
  def preInit(e: FMLPreInitializationEvent): Unit = {
    DimensionalLayersWorldType.getName
  }

  @EventHandler
  def postInit(e: FMLPostInitializationEvent): Unit = {
    DimensionalLayersPreset("")
  }


  @SideOnly(Side.CLIENT)
  @SubscribeEvent(priority = EventPriority.LOWEST)
  def onGuiCreateWorld(event: ActionPerformedEvent): Unit =
    if (Configuration.worldTypeByDefault)
      event.getGui match {
        case guiCreateWorld: GuiCreateWorld =>
          if (guiCreateWorld.worldSeed.isEmpty)
            if (event.getButton == guiCreateWorld.btnMoreOptions)
              guiCreateWorld.selectedIndex = DimensionalLayersWorldType.getId

        case _ =>
      }
}
