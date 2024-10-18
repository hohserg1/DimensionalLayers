package hohserg.dimensional.layers.gui.add.cubic.worldtype

import hohserg.dimensional.layers.DimensionalLayersWorldType
import hohserg.dimensional.layers.gui._
import hohserg.dimensional.layers.gui.add.cubic.worldtype.GuiAddLayer.possibleWorldTypes
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.preset.spec.CubicWorldTypeLayerSpec
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import net.minecraft.client.renderer.Tessellator
import net.minecraft.world.WorldType
import net.minecraftforge.fml.relauncher.{Side, SideOnly}


@SideOnly(Side.CLIENT)
object GuiAddLayer {
  lazy val possibleWorldTypes =
    WorldType.WORLD_TYPES
      .toIndexedSeq
      .filter(_ != null)
      .filter(_.canBeCreated)
      .collect { case cubic: WorldType with ICubicWorldType => cubic }
      .filter(_ != DimensionalLayersWorldType)
}

@SideOnly(Side.CLIENT)
class GuiAddLayer(parent: GuiSetupDimensionalLayersPreset) extends GuiBase(parent) {
  override def initGui(): Unit = {
    super.initGui()
    addButton(new GuiClickableButton(width - 100, height - 30, 90, 20, "Cancel")(back))
    addElement(new GuiScrollingListElement(10, 10, width - 200, height - 20, fontRenderer.FONT_HEIGHT + 4) {
      override def getSize: Int = possibleWorldTypes.size

      override def elementClicked(index: Int, doubleClick: Boolean): Unit = {
        if (possibleWorldTypes.indices contains index) {
          parent.layersList.add(CubicWorldTypeLayerSpec(possibleWorldTypes(index)))
          back()
        }
      }

      override def isSelected(index: Int): Boolean = false

      override def drawBackground(): Unit = ()

      override def drawSlot(index: Int, right: Int, top: Int, height: Int, tess: Tessellator): Unit = {
        if (possibleWorldTypes.indices contains index) {
          val worldType = possibleWorldTypes(index)
          fontRenderer.drawString(makeWorldTypeLabel(worldType), left + 3, top + 1, 0xffff00ff)

          val (mx, my) = MouseUtils.getMousePos
          if (left <= mx && mx <= right &&
            top <= my && my <= top + height) {
            drawHighlight(left + 2, top, right - left - 2, height)
          }
        }
      }
    })
  }
}
