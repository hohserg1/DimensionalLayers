package hohserg.dimensional.layers.gui.preset

import hohserg.dimensional.layers.Main
import hohserg.dimensional.layers.gui.add.*
import hohserg.dimensional.layers.gui.preset.list.GuiLayersList
import hohserg.dimensional.layers.gui.{AccessorGuiScrollingList, GuiBase, GuiClickableButton}
import hohserg.dimensional.layers.lens.GuiCreateWorldLens
import hohserg.dimensional.layers.preset.CubicWorldTypeHelper
import net.minecraft.client.gui.GuiCreateWorld
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import java.util.Random

@SideOnly(Side.CLIENT)
class GuiSetupDimensionalLayersPreset(parent: GuiCreateWorld) extends GuiBase(parent) {
  var layersList: GuiLayersList = null
  var exportButton: GuiClickableButton = null
  var importButton: GuiClickableButton = null
  var doneButton: GuiClickableButton = null

  override protected def back(): Unit = {
    if (GuiCreateWorldLens.worldSeed.get(parent).isEmpty)
      GuiCreateWorldLens.worldSeed.set(parent, new Random().nextLong.toString)
    super.back()
  }

  override def initGui(): Unit = {
    super.initGui()

    doneButton = addButton(new GuiClickableButton(width - 80 - 10, 10, 80, 20, "Done")(() => {
      parent.chunkProviderSettingsJson = layersList.toSettings
      back()
    }))

    addButton(new GuiClickableButton(width - 80 - 10 - 80 - 10, 10, 80, 20, "Cancel")(back))

    addButton(new GuiClickableButton(width - 150 - 10, 10 + 20 + 10, 150, 20, "Add dimension layer")(show(new dimension.GuiAddLayer(_))))

    addButton(new GuiClickableButton(width - 150 - 10, 10 + 20 + 10 + 20 + 1, 150, 20, "Add solid layer")(show(new solid.GuiAddLayer(_))))

    addButton(new GuiClickableButton(width - 150 - 10, 10 + 20 + 10 + 20 + 1 + 20 + 1, 150, 20, "Add cubic world type layer")(
      if (CubicWorldTypeHelper.possibleWorldTypes.nonEmpty)
        show(new cubic.worldtype.GuiAddLayer(_))
      else
        showWarning("Need to install", "CubicWorldGen or smth like")
    ))
    /*
    addButton(new GuiClickableButton(width - 150 - 10, 10 + 20 + 10 + 20 + 1 + 20 + 1 + 20 + 1, 150, 20, "Add OTG layer")(
      if (Main.otgPresent)
        show(new otg.GuiAddLayer(_))
      else
        showWarning("Need to install", "Open Terrain Generator")
    ))
     */

    importButton = addButton(new GuiClickableButton(width - 110 - 10, height - 30, 110, 20, "Import preset")(show(new GuiImportPreset(_))))
    exportButton = addButton(new GuiClickableButton(width - 110 - 10, height - 30 - 20 - 1, 110, 20, "Export preset")(GuiImportPreset.exportPreset(this)))

    initFromJson(if (layersList == null) parent.chunkProviderSettingsJson else layersList.toSettings)
  }

  def initFromJson(preset: String): Unit = {
    layersList = addElement(new GuiLayersList(this, preset, if (layersList == null) 0 else AccessorGuiScrollingList.scrollDistance.get(layersList)))
  }

  override def drawScreenPre(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreenPre(mouseX, mouseY, partialTicks)
    val isValidPreset = layersList.entries.nonEmpty
    exportButton.enabled = isValidPreset
    doneButton.enabled = isValidPreset
  }
}
