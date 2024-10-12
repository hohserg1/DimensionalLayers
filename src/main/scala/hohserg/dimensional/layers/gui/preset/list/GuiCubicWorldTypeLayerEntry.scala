package hohserg.dimensional.layers.gui.preset.list

import hohserg.dimensional.layers.gui.GuiBase
import hohserg.dimensional.layers.gui.IconUtils._
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings._
import hohserg.dimensional.layers.preset.CubicWorldTypeLayerSpec
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class GuiCubicWorldTypeLayerEntry(val parent: GuiLayersList, val layer: CubicWorldTypeLayerSpec) extends GuiLayerEntry {
  override def drawEntry(index: Int, minX: Int, minY: Int, maxX: Int, maxY: Int, mouseX: Int, mouseY: Int): Unit = {
    super.drawEntry(index, minX, minY, maxX, maxY, mouseX, mouseY)
    drawLogo(layer.cubicWorldType, minX, minY)
    drawLogo(layer.dimensionType1, minX + 32, minY + 32, 32)

    mc.fontRenderer.drawStringWithShadow(layer.cubicWorldType.getName, minX + width + 11, minY + (maxY - minY) / 2 + 4, 0xffffff)
  }

  override def guiSettings(index: Int, parent: GuiSetupDimensionalLayersPreset): GuiBase = new cubic.worldtype.GuiSettingsLayer(parent, layer, index)
}
