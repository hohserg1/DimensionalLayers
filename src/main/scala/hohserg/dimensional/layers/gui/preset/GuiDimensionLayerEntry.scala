package hohserg.dimensional.layers.gui.preset

import hohserg.dimensional.layers.DimensionLayersPreset.DimensionLayerSpec
import hohserg.dimensional.layers.gui.DimensionLogo
import net.minecraft.util.ResourceLocation

import java.awt.Rectangle

object GuiDimensionLayerEntry {
  final val height = DimensionLogo.width
  val moveUp = new Rectangle(DimensionLogo.width, 0, 11, 7)
  val moveDown = new Rectangle(DimensionLogo.width, height - 7, 11, 7)
  val texture = new ResourceLocation("textures/gui/server_selection.png")
}

class GuiDimensionLayerEntry(val parent: GuiLayersList, val layer: DimensionLayerSpec) extends GuiLayerEntry {
  override def drawEntry(index: Int, x: Int, y: Int, mouseX: Int, mouseY: Int): Unit = {
    super.drawEntry(index, x, y, mouseX, mouseY)
    DimensionLogo.draw(layer.dimensionType, x, y)
    mc.fontRenderer.drawStringWithShadow(layer.dimensionType.getName, x + DimensionLogo.width + 4, y + height / 2 - 5, 0xffffff)
  }
}
