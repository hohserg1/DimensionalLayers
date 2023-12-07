package hohserg.dimensional.layers.gui.settings.dimension

import net.minecraft.client.gui.GuiCreateWorld
import net.minecraftforge.client.event.GuiOpenEvent

class GuiFakeCreateWorld(val parent: GuiSettingsLayer, worldTypePreset: String) extends GuiCreateWorld(parent) {
  chunkProviderSettingsJson = worldTypePreset

  def hasChanges = chunkProviderSettingsJson != worldTypePreset
}

object GuiFakeCreateWorld {
  def replaceGuiByParent(e: GuiOpenEvent): Unit = {
    e.getGui match {
      case gui: GuiFakeCreateWorld =>
        e.setGui(gui.parent)
        if (gui.hasChanges)
          gui.parent.markChanged()
      case _ =>
    }
  }
}
