package hohserg.dimensional.layers.gui.preset

import hohserg.dimensional.layers.Main
import hohserg.dimensional.layers.gui.add._
import hohserg.dimensional.layers.gui.preset.list.GuiLayersList
import hohserg.dimensional.layers.gui.{AccessorGuiScrollingList, GuiBase, GuiClickableButton}
import net.minecraft.client.gui.GuiCreateWorld
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import java.util.Random

@SideOnly(Side.CLIENT)
class GuiSetupDimensionalLayersPreset(parent: GuiCreateWorld) extends GuiBase(parent) {
  var layersList: GuiLayersList = _
  var exportButton: GuiClickableButton = _
  var importButton: GuiClickableButton = _
  var doneButton: GuiClickableButton = _

  override protected def back(): Unit = {
    if (parent.worldSeed.isEmpty)
      parent.worldSeed = new Random().nextLong.toString
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
      if (cubic.worldtype.GuiAddLayer.possibleWorldTypes.nonEmpty)
        show(new cubic.worldtype.GuiAddLayer(_))
      else
        showWarning("Need to install", "CubicWorldGen or smth like")
    ))
    addButton(new GuiClickableButton(width - 150 - 10, 10 + 20 + 10 + 20 + 1 + 20 + 1 + 20 + 1, 150, 20, "Add OTG layer")(
      if (Main.otgPresent)
        show(new otg.GuiAddLayer(_))
      else
        showWarning("Need to install", "Open Terrain Generator")
    ))

    importButton = addButton(new GuiClickableButton(width - 110 - 10, height - 30, 110, 20, "Import preset")(show(new GuiImportPreset(_))))
    exportButton = addButton(new GuiClickableButton(width - 110 - 10, height - 30 - 20 - 1, 110, 20, "Export preset")(GuiImportPreset.export(this)))

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
