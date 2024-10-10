package hohserg.dimensional.layers.gui.preset.list

import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.solid
import hohserg.dimensional.layers.gui.settings.solid.GuiBlocksList.DrawableBlock
import hohserg.dimensional.layers.gui.{GuiBase, IconUtils}
import hohserg.dimensional.layers.preset.SolidLayerSpec
import net.minecraft.client.renderer.{GlStateManager, RenderHelper}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class GuiSolidLayerEntry(val parent: GuiLayersList, val layer: SolidLayerSpec) extends GuiLayerEntry {

  val block = DrawableBlock(layer.filler.getBlock)

  override def drawEntry(index: Int, minX: Int, minY: Int, maxX: Int, maxY: Int, mouseX: Int, mouseY: Int): Unit = {
    super.drawEntry(index, minX, minY, maxX, maxY, mouseX, mouseY)
    mc.fontRenderer.drawStringWithShadow(block.tooltip, minX + IconUtils.width + 11, minY + (maxY - minY) / 2 - 10, 0xffffff)
    mc.fontRenderer.drawStringWithShadow("height: " + layer.height + " cubes", minX + IconUtils.width + 11, minY + (maxY - minY) / 2, 0xffffff)
    RenderHelper.enableGUIStandardItemLighting()
    val centered = 64 / 2 - 32 / 2
    val x = minX + centered
    val y = minY + centered
    GlStateManager.translate(x, y, 0)
    GlStateManager.scale(2, 2, 2)
    block.draw(0, 0, 0, 0)
    GlStateManager.scale(0.5, 0.5, 0.5)
    GlStateManager.translate(-x, -y, 0)
  }

  override def guiSettings(index: Int, prevGui: GuiSetupDimensionalLayersPreset): GuiBase = new solid.GuiSettingsLayer(prevGui, layer, index)
}
