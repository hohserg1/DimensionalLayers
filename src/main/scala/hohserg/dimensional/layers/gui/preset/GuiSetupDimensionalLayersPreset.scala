package hohserg.dimensional.layers.gui.preset

import hohserg.dimensional.layers.gui.add.*
import hohserg.dimensional.layers.gui.preset.list.{GuiLayersList, texture}
import hohserg.dimensional.layers.gui.whole.world.GuiSelectRealDimensionForEdit
import hohserg.dimensional.layers.gui.{DrawableArea, GuiBase, GuiClickableButton, GuiScrollingListLens, RelativeCoord}
import hohserg.dimensional.layers.lens.GuiCreateWorldLens
import hohserg.dimensional.layers.preset.CubicWorldTypeHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiCreateWorld
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11

import java.awt.Rectangle
import java.util.Random

@SideOnly(Side.CLIENT)
class GuiSetupDimensionalLayersPreset(parent: GuiCreateWorld) extends GuiBase(parent) {
  var currentRealDimension: Int = 0

  var layersList: GuiLayersList = null
  var exportButton: GuiClickableButton = null
  var importButton: GuiClickableButton = null
  var doneButton: GuiClickableButton = null
  var changeRealDimensionButton: GuiChangeRealDimensionButton = null

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

    addButton(new GuiClickableButton(x = width - 80 - 10 - 80 - 10, y = 10, w = 80, h = 20, label = "Cancel")(back))

    val addStartY = 10 + 20 + 10
    val addStep = 20 + 1

    addButton(new GuiClickableButton(x = width - 150 - 10, y = addStartY, w = 150, h = 20, label = "Add dimension layer")(show(new dimension.GuiAddLayer(_))))

    addButton(new GuiClickableButton(x = width - 150 - 10, y = addStartY + addStep, w = 150, h = 20, label = "Add solid layer")(show(new solid.GuiAddLayer(_))))

    addButton(new GuiClickableButton(x = width - 150 - 10, y = addStartY + addStep * 2, w = 150, h = 20, label = "Add cubic world type layer")(
      if (CubicWorldTypeHelper.possibleWorldTypes.nonEmpty)
        show(new cubic.worldtype.GuiAddLayer(_))
      else
        showWarning("Need to install", "CubicWorldGen or smth like")
    ))

    addButton(new GuiClickableButton(x = width - 150 - 10, y = addStartY + addStep * 3, w = 150, h = 20, label = "Add mistcraft layer")(show(new mystcraft.GuiAddLayer(_))))
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

    changeRealDimensionButton = addButton(new GuiChangeRealDimensionButton(width - 80 - 10 - 80 - 10, height - 10 - 33))
  }

  def initFromJson(preset: String): Unit = {
    layersList = addElement(new GuiLayersList(this, preset, if (layersList == null) 0 else GuiScrollingListLens.scrollDistance.get(layersList)))
  }

  override def drawScreenPre(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreenPre(mouseX, mouseY, partialTicks)
    val isValidPreset = layersList.entries.nonEmpty
    exportButton.enabled = isValidPreset
    doneButton.enabled = isValidPreset
  }

  override def drawScreenPost(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreenPost(mouseX, mouseY, partialTicks)
    changeRealDimensionButton.renderTooltip(mouseX, mouseY)
  }
}
