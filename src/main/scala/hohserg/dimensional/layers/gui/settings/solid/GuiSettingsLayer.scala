package hohserg.dimensional.layers.gui.settings.solid

import hohserg.dimensional.layers.clamp
import hohserg.dimensional.layers.gui.GuiNumericField.NumberHolder
import hohserg.dimensional.layers.gui.GuiTileList.SelectHandler
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionLayersPreset
import hohserg.dimensional.layers.gui.settings.solid.GuiBlocksList.DrawableBlock
import hohserg.dimensional.layers.gui.{GuiBase, GuiClickableButton, GuiNumericField, MouseUtils}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.init.{Biomes, Blocks}
import net.minecraft.world.biome.Biome

import scala.collection.JavaConverters.mapAsScalaMapConverter

class GuiSettingsLayer(parent: GuiSetupDimensionLayersPreset, onFinish: (IBlockState, Biome, Int) => Unit, initBlock: IBlockState = Blocks.AIR.getDefaultState, initHeight: Int = 1)
  extends GuiBase(parent) with SelectHandler[GuiBlocksList.DrawableBlock] {
  val layerHeight = new NumberHolder[Int](initHeight) {
    override def validate(v: Int): Int = clamp(v, 1, 99)
  }

  var blocksList: GuiBlocksList = _
  var doneButton: GuiClickableButton = _
  var heightField: GuiNumericField[Int] = _

  var currentBlockState = initBlock

  var rightPaneMinX = 0

  override def initGui(): Unit = {
    super.initGui()
    rightPaneMinX = width - 190

    addButton(new GuiClickableButton(0, width - 100, height - 30, 90, 20, "Cancel")(back))

    doneButton = addButton(new GuiClickableButton(1, width - 100, 10, 90, 20, "Done")(() => {
      onFinish(currentBlockState, Biomes.PLAINS, layerHeight.get)
      back()
    }
    ) {
      enabled = false
    })

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
    if (initHeight.toString != heightField.getText)
      doneButton.enabled = true
  }

  override def onSelected(item: DrawableBlock): Unit = {
    doneButton.enabled = true
    if (currentBlockState.getBlock != item.block) {
      currentBlockState = item.block.getDefaultState
    }
  }
}
