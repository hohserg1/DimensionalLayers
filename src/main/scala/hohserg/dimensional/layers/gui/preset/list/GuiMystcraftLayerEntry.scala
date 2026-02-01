package hohserg.dimensional.layers.gui.preset.list

import hohserg.dimensional.layers.gui.IconUtils.drawLogo
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.mystcraft.{DrawableSymbol, GuiSettingsLayer}
import hohserg.dimensional.layers.gui.{GuiBase, makeDimensionTypeLabel}
import hohserg.dimensional.layers.preset.spec.MystcraftLayerSpec
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class GuiMystcraftLayerEntry(val parent: GuiLayersList, val layer: MystcraftLayerSpec) extends GuiLayerEntry {
  val icon = DrawableSymbol(layer.symbols.head)
  override def drawEntry(index: Int, minX: Int, minY: Int, maxX: Int, maxY: Int, mouseX: Int, mouseY: Int): Unit = {
    super.drawEntry(index, minX, minY, maxX, maxY, mouseX, mouseY)
    val h = maxY-minY
    icon.draw(minX,minY,minX+h,maxY)
    mc.fontRenderer.drawStringWithShadow("Mystcraft", minX + h + 11, minY + (maxY - minY) / 2 - 5, 0xffffff)
  }

  override def guiSettings(index: Int, prevGui: GuiSetupDimensionalLayersPreset): GuiBase = new GuiSettingsLayer(prevGui, layer, index)
}
