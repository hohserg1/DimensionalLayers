package hohserg.dimensional.layers.gui.add.otg

import com.pg85.otg.configuration.dimensions.DimensionConfigGui
import com.pg85.otg.forge.gui.GuiHandler
import com.pg85.otg.forge.gui.presets.OTGPresetInfoPanel
import hohserg.dimensional.layers.gui._
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.preset.spec.OpenTerrainGeneratorLayerSpec
import net.minecraft.client.renderer.Tessellator

import scala.collection.JavaConverters._

class GuiAddLayer(parent: GuiSetupDimensionalLayersPreset) extends GuiBase(parent) {
  var hoveringPreset: Option[String] = None

  override def initGui(): Unit = {
    super.initGui()
    GuiHandler.loadGuiPresets()

    addButton(new GuiClickableButton(width - 100, height - 30, 90, 20, "Cancel")(back))

    addElement(new OTGPresetInfoPanel(this))

    addElement(new GuiScrollingListElement(10, 37, 150, height - 73, fontRenderer.FONT_HEIGHT + 6) {
      val possibles: Seq[(Some[String], DimensionConfigGui)] =
        GuiHandler.GuiPresets.asScala.toIndexedSeq
          .filter(_._2.ShowInWorldCreationGUI)
          .map { case (a, b) => Some(a) -> b }

      override def getSize: Int = possibles.size

      override def elementClicked(index: Int, doubleClick: Boolean): Unit = {
        if (possibles.indices contains index) {
          parent.layersList.add(OpenTerrainGeneratorLayerSpec(possibles(index)._1.get))
          back()
        }
      }

      override def isSelected(index: Int): Boolean = false

      override def drawBackground(): Unit = ()

      override def drawSlot(index: Int, right: Int, top: Int, height: Int, tess: Tessellator): Unit = {
        if (possibles.indices contains index) {
          val preset = possibles(index)
          fontRenderer.drawString(preset._1.get, left + 3, top + 1, 0xffff00ff)

          val (mx, my) = MouseUtils.getMousePos
          if (left <= mx && mx <= right &&
            top <= my && my <= top + height) {
            hoveringPreset = preset._1
            drawHighlight(left + 2, top, right - left - 2, height)
          }
        }
      }
    })
  }

  override def drawScreenPost(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreenPost(mouseX, mouseY, partialTicks)
    drawCenteredString(fontRenderer, "Select a preset", this.width / 2, 16, 0xffFFffFF)
  }
}
