package hohserg.dimensional.layers.gui.settings.solid

import hohserg.dimensional.layers.DimensionalLayersPreset
import hohserg.dimensional.layers.DimensionalLayersPreset.SolidLayerSpec
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.GuiTileList.SelectHandler
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer
import hohserg.dimensional.layers.gui.settings.solid.GuiBlocksList.DrawableBlock
import hohserg.dimensional.layers.gui.{GuiNumericField, MouseUtils}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.world.biome.Biome

import scala.collection.JavaConverters.mapAsScalaMapConverter

class GuiSettingsLayer(parent: GuiSetupDimensionalLayersPreset, layer: SolidLayerSpec, index: Int)
  extends GuiBaseSettingsLayer(parent, index) with SelectHandler[GuiBlocksList.DrawableBlock] {

  val fillerH = new ValueHolder[IBlockState](layer.filler)
  val biomeH = new ValueHolder[Biome](layer.biome)
  val heightH = new ValueHolder[Int](layer.height)

  override def buildLayerSpec(): DimensionalLayersPreset.LayerSpec = SolidLayerSpec(fillerH.get, biomeH.get, heightH.get)

  var blocksList: GuiBlocksList = _
  var heightField: GuiNumericField[Int] = _

  var rightPaneMinX = 0

  override def initGui(): Unit = {
    super.initGui()
    rightPaneMinX = width - 190

    blocksList = new GuiBlocksList(this, width - 210, height)
    blocksList.select(GuiBlocksList.DrawableBlock(fillerH.get.getBlock))

    heightField = new GuiNumericField(rightPaneMinX + fontRenderer.getStringWidth("height:") + 3, doneButton.y + doneButton.height + 10, 2, heightH, _.toInt)
  }

  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    drawDefaultBackground()
    super.drawScreen(mouseX, mouseY, partialTicks)
    blocksList.drawScreen(mouseX, mouseY, partialTicks)
    RenderHelper.disableStandardItemLighting()
    drawString(fontRenderer, "height:", rightPaneMinX, heightField.y + 5, 0xffa0a0a0)
    drawString(fontRenderer, "(cubes)", heightField.x + heightField.width + 4, heightField.y + 5, 0xffa0a0a0)
    heightField.drawTextBox()

    drawString(fontRenderer, "properties: ", rightPaneMinX, heightField.y + heightField.height + 20, 0xffa0a0a0)
    fillerH.get.getProperties.asScala.toSeq.zipWithIndex.foreach {
      case ((prop, value), index) =>
        drawString(fontRenderer, prop.getName + ": " + value, rightPaneMinX + 10, heightField.y + heightField.height + 30 + index * 10, 0xffa0a0a0)
    }
  }

  override def handleMouseInput(): Unit = {
    super.handleMouseInput()
    val (mouseX, mouseY) = MouseUtils.getMousePos
    blocksList.handleMouseInput(mouseX, mouseY)
  }

  override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
    super.mouseClicked(mouseX, mouseY, mouseButton)
    heightField.mouseClicked(mouseX, mouseY, mouseButton)
  }

  override def keyTyped(typedChar: Char, keyCode: Int): Unit = {
    super.keyTyped(typedChar, keyCode)
    heightField.textboxKeyTyped(typedChar, keyCode)
  }

  override def onSelected(item: DrawableBlock): Unit = {
    if (fillerH.get.getBlock != item.block) {
      fillerH.set(item.block.getDefaultState)
    }
  }
}
