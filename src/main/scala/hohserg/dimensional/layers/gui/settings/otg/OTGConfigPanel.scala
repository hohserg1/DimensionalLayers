package hohserg.dimensional.layers.gui.settings.otg

import com.pg85.otg.forge.gui.dimensions.base.CategoryEntry
import com.pg85.otg.forge.gui.dimensions.{OTGGuiDimensionList, OTGGuiDimensionSettingsList}
import com.pg85.otg.forge.gui.presets.OTGGuiPresetList
import hohserg.dimensional.layers.gui.GuiElement
import hohserg.dimensional.layers.gui.settings.otg
import net.minecraft.client.Minecraft
import net.minecraft.util.Tuple

class OTGConfigPanel(parent: otg.GuiSettingsLayer) extends GuiElement {
  private val fakeParent1 = new OTGGuiPresetList(null, true)
  fakeParent1.setWorldAndResolution(Minecraft.getMinecraft, parent.width, parent.height)
  fakeParent1.selectedPreset = new Tuple(parent.layer.presetName, parent.layer.toOTGConfigClient)

  private val fakeParent = new OTGGuiDimensionList(fakeParent1)
  fakeParent.setWorldAndResolution(Minecraft.getMinecraft, parent.width, parent.height)
  val settingsList = new OTGGuiDimensionSettingsList(fakeParent, 10, parent.height - 10, 10, parent.width - 110, Minecraft.getMinecraft) {
    override def applySettings(): Unit = {
      super.applySettings()
      fakeParent.selectedDimension.ShowInWorldCreationGUI = false
      parent.configYmlH.set(fakeParent.selectedDimension.toYamlString)
    }

    override def refreshData(mainMenu: Boolean, gameRulesMenu: Boolean, advancedSettingsMenu: Boolean): Unit = {
      super.refreshData(mainMenu, gameRulesMenu, advancedSettingsMenu)
      if (mainMenu) {
        getAllListEntries.remove(8)
        getAllListEntries.remove(7)
        getAllListEntries.remove(5)
        getAllListEntries.remove(3)
        getAllListEntries.remove(1)
      } else if (gameRulesMenu) {
        getAllListEntries.remove(getAllListEntries.size() - 3)
        getAllListEntries.set(getAllListEntries.size() - 2, new CategoryEntry(this, ""))

      } else if (advancedSettingsMenu) {

      }
    }
  }

  override def draw: Option[(Int, Int, Float) => Unit] = Some(settingsList.drawScreen)

  override def mouseInput: Option[(Int, Int) => Unit] = Some((_, _) => settingsList.handleMouseInput())

  override def mouseClick: Option[(Int, Int, Int) => Unit] = Some(settingsList.mouseClicked)

  override def mouseRelease: Option[(Int, Int, Int) => Unit] = Some(settingsList.mouseReleased)

  override def keyTyped: Option[(Char, Int) => Unit] = Some(settingsList.keyTyped)
}
