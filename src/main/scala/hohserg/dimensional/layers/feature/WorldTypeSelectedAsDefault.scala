package hohserg.dimensional.layers.feature

import hohserg.dimensional.layers.lens.GuiCreateWorldLens
import hohserg.dimensional.layers.{Configuration, DimensionalLayersWorldType}
import net.minecraft.client.gui.GuiCreateWorld
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
@EventBusSubscriber(Array(Side.CLIENT))
object WorldTypeSelectedAsDefault {

  @SideOnly(Side.CLIENT)
  @SubscribeEvent(priority = EventPriority.LOW)
  def onGuiCreateWorld(event: GuiOpenEvent): Unit = {
    if (Configuration.worldTypeByDefault)
      event.getGui match {
        case guiCreateWorld: GuiCreateWorld =>
          if (GuiCreateWorldLens.worldSeed.get(guiCreateWorld).isEmpty)
            GuiCreateWorldLens.selectedIndex.set(guiCreateWorld, DimensionalLayersWorldType.getId)
        case _ =>
      }
  }
}
