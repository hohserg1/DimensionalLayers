package hohserg.dimensional.layers.gui.settings

import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import net.minecraft.client.gui.GuiCreateWorld
import net.minecraftforge.client.event.GuiOpenEvent

class GuiFakeCreateWorld(val parent: GuiBaseSettingsLayer {val worldTypePresetH: ValueHolder[String]}, worldTypePreset: String) extends GuiCreateWorld(parent) {
  chunkProviderSettingsJson = worldTypePreset
}

object GuiFakeCreateWorld {
  def replaceGuiByParent(e: GuiOpenEvent): Unit = {
    e.getGui match {
      case gui: GuiFakeCreateWorld =>
        gui.parent.worldTypePresetH.set(gui.chunkProviderSettingsJson)
        e.setGui(gui.parent)
      case _ =>
    }
  }
}
