package hohserg.dimensional.layers.gui.settings

import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import net.minecraft.client.gui.GuiCreateWorld
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class GuiFakeCreateWorld(val parent: GuiBaseSettingsLayer {val worldTypePresetH: ValueHolder[String]}, worldTypePreset: String) extends GuiCreateWorld(parent) {
  chunkProviderSettingsJson = worldTypePreset
}

@SideOnly(Side.CLIENT)
@EventBusSubscriber(Array(Side.CLIENT))
object GuiFakeCreateWorldSupport {

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  def replaceGuiByParent(e: GuiOpenEvent): Unit = {
    e.getGui match {
      case gui: GuiFakeCreateWorld =>
        gui.parent.worldTypePresetH.set(gui.chunkProviderSettingsJson)
        e.setGui(gui.parent)
      case _ =>
    }
  }
}
