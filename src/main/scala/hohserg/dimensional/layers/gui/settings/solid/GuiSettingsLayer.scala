package hohserg.dimensional.layers.gui.settings.solid

import com.google.common.base.Predicate
import hohserg.dimensional.layers.gui.GuiTileList.SelectHandler
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionLayersPreset
import hohserg.dimensional.layers.gui.settings.solid.GuiBlocksList.DrawableBlock
import hohserg.dimensional.layers.gui.{GuiBase, GuiClickableButton, MouseUtils}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.init.{Biomes, Blocks}
import net.minecraft.util.math.MathHelper.clamp
import net.minecraft.world.biome.Biome

import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.util.Try

class GuiSettingsLayer(parent: GuiSetupDimensionLayersPreset, onFinish: (IBlockState, Biome, Int) => Unit, initBlock: IBlockState = Blocks.AIR.getDefaultState, initHeight: Int = 1)
  extends GuiBase(parent) with SelectHandler[GuiBlocksList.DrawableBlock] {
  var blocksList: GuiBlocksList = _
  var doneButton: GuiClickableButton = _
  var heightField: GuiTextField = _

  var currentBlockState = initBlock

  var rightPaneMinX = 0

  override def initGui(): Unit = {
    rightPaneMinX = width - 190

    addButton(new GuiClickableButton(0, width - 100, height - 30, 90, 20, "Cancel")(back))

    doneButton = addButton(new GuiClickableButton(1, width - 100, 10, 90, 20, "Done")(() => {
      onFinish(currentBlockState, Biomes.PLAINS, Try(heightField.getText.toInt).map(clamp(_, 1, 99)).getOrElse(1))
      back()
    }
    ) {
      enabled = false
    })

    blocksList = new GuiBlocksList(this, width - 210, height)
    if (currentBlockState != Blocks.AIR.getDefaultState)
      blocksList.select(GuiBlocksList.DrawableBlock(currentBlockState.getBlock))

    heightField = new GuiTextField(2, fontRenderer, rightPaneMinX + fontRenderer.getStringWidth("height:") + 3, doneButton.y + doneButton.height + 10, fontRenderer.getStringWidth("99") + 8, 18)
    heightField.setText(initHeight.toString)
    heightField.setMaxStringLength(2)
    heightField.setValidator(new Predicate[String] {
      override def apply(input: String): Boolean = input.isEmpty || Try(input.toInt).isSuccess
    })

  }

  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    drawDefaultBackground()
    super.drawScreen(mouseX, mouseY, partialTicks)
    blocksList.drawScreen(mouseX, mouseY, partialTicks)
    RenderHelper.disableStandardItemLighting()
    drawString(fontRenderer, "height:", rightPaneMinX, heightField.y + 5, 0xffffffff)
    drawString(fontRenderer, "(cubes)", heightField.x + heightField.width + 4, heightField.y + 5, 0xffffffff)
    heightField.drawTextBox()

    fontRenderer.drawString("properties: ", rightPaneMinX, heightField.y + heightField.height + 20, 0xffff00ff)
    currentBlockState.getProperties.asScala.toSeq.zipWithIndex.foreach {
      case ((prop, value), index) =>
        fontRenderer.drawString(prop.getName + ": " + value, rightPaneMinX + 10, heightField.y + heightField.height + 30 + index * 10, 0xffff00ff)
    }
  }

  override def handleMouseInput(): Unit = {
    super.handleMouseInput()
    val (mouseX, mouseY) = MouseUtils.getMousePos(parent)
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
