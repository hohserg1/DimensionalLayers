package hohserg.dimensional.layers.gui.whole.world

import hohserg.dimensional.layers.gui.{DrawableArea, GuiBase, GuiClickableButton, GuiScrollingListElement, drawHighlightHovering}
import hohserg.dimensional.layers.preset.DimensionalLayersPreset
import net.minecraft.client.renderer.Tessellator
import net.minecraftforge.common.DimensionManager

import scala.jdk.CollectionConverters.*

class GuiNewRealDimension(parent: GuiSelectRealDimensionForEdit) extends GuiBase(parent) {

  val possibles = DimensionManager.getRegisteredDimensions.asScala
                                  .flatMap { case (dimType, ids) => ids.asScala.map(id => id.intValue() -> dimType) }
                                  .filter { case (id, _) => !parent.preset.realDimensionToLayers.contains(id) }
                                  .toSet.toSeq.sorted

  override def initGui(): Unit = {
    super.initGui()
    addButton(new GuiClickableButton(width - 80 - 10, 10, 80, 20, "Cancel")(back))

    addElement(new GuiScrollingListElement(10, 10, math.max(100, width - 80 - 15 - 10), height - 10, fontRenderer.FONT_HEIGHT + 6) {

      override def getSize: Int = possibles.size

      override def elementClicked(index: Int, doubleClick: Boolean): Unit = {
        if (possibles.indices contains index) {
          val id = possibles(index)._1
          parent.preset = parent.preset.copy(realDimensionToLayers = parent.preset.realDimensionToLayers + (id -> DimensionalLayersPreset.singleMixedPreset))
          parent.initGui()
          back()
        }
      }

      override def isSelected(i: Int): Boolean = false

      override def drawBackground(): Unit = ()

      override def drawSlot(index: Int, right: Int, top: Int, height: Int, tess: Tessellator): Unit = {
        if (possibles.indices contains index) {
          val (id, dimType) = possibles(index)
          fontRenderer.drawString(id.toString + ": " + dimType.getName, left + 3, top + 2, 0xffff00ff)
          if (DrawableArea.isHovering(left + 1, top, right, top + height))
            drawHighlightHovering(left + 1, top, right - left - 1, height)
        }
      }
    })
  }

}
