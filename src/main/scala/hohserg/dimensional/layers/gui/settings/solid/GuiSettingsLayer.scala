package hohserg.dimensional.layers.gui.settings.solid

import hohserg.dimensional.layers.DimensionalLayersPreset.SolidLayerSpec
import hohserg.dimensional.layers.gui.GuiNumericField.NumberHolder
import hohserg.dimensional.layers.gui.GuiTileList.SelectHandler
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer
import hohserg.dimensional.layers.gui.settings.solid.GuiBlocksList.DrawableBlock
import hohserg.dimensional.layers.gui.{GuiNumericField, MouseUtils}
import hohserg.dimensional.layers.{DimensionalLayersPreset, clamp}
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.init.{Biomes, Blocks}

import scala.collection.JavaConverters._

class GuiSettingsLayer(parent: GuiSetupDimensionalLayersPreset, layer: SolidLayerSpec, index: Int)
  extends GuiBaseSettingsLayer(parent, layer, index) with SelectHandler[GuiBlocksList.DrawableBlock] {

  override def buildLayerSpec(): DimensionalLayersPreset.LayerSpec = SolidLayerSpec(currentBlockState, Biomes.PLAINS, layerHeight.get)

  val layerHeight = new NumberHolder[Int](layer.height) {
    override def validate(v: Int): Int = clamp(v, 1, 99)
  }

  var blocksList: GuiBlocksList = _
  var heightField: GuiNumericField[Int] = _

  var currentBlockState = layer.filler

  var rightPaneMinX = 0

  override def initGui(): Unit = {
    super.initGui()
    rightPaneMinX = width - 190

    blocksList = new GuiBlocksList(this, width - 210, height)
    if (currentBlockState != Blocks.AIR.getDefaultState)
      blocksList.select(GuiBlocksList.DrawableBlock(currentBlockState.getBlock))

    heightField = new GuiNumericField(2, rightPaneMinX + fontRenderer.getStringWidth("height:") + 3, doneButton.y + doneButton.height + 10, 2, layerHeight, _.toInt)
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
    currentBlockState.getProperties.asScala.toSeq.zipWithIndex.foreach {
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
    if (layer.height.toString != heightField.getText)
      markChanged()
  }

  override def onSelected(item: DrawableBlock): Unit = {
    markChanged()
    if (currentBlockState.getBlock != item.block) {
      currentBlockState = item.block.getDefaultState
    }
  }
}
