package hohserg.dimensional.layers.gui.preset

import com.google.gson.{GsonBuilder, JsonParser}
import hohserg.dimensional.layers.gui.{GuiBase, GuiClickableButton}
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard

import scala.util.Try

class GuiImportPreset(parent: GuiSetupDimensionalLayersPreset) extends GuiBase(parent) {
  var textArea: GuiMultiLineTextField = _

  override def done(): Unit = {
    parent.initFromJson(textArea.getText)
    super.done()
  }


  override def initGui(): Unit = {
    super.initGui()
    val prevTest = if (textArea == null) parent.layersList.toSettings else textArea.getText

    textArea = new GuiMultiLineTextField(0, 10, 10 + 15, width - 10 - 10 - 80 - 10, height - 10 - 15 - 10, prevTest)
    Keyboard.enableRepeatEvents(true)
    textArea.setMaxStringLength(10000)
    textArea.setText(prevTest)
    textArea.setCanLoseFocus(false)
    textArea.setFocused(true)
    textArea.setEnableBackgroundDrawing(true)

    addButton(new GuiClickableButton(1, width - 80 - 10, height / 2 - 10, 80, 20, "Beautify")(beautify))

    addButton(new GuiClickableButton(0, width - 100, height - 30, 90, 20, "Cancel")(back))

    addButton(new GuiClickableButton(1, width - 100, 10, 90, 20, "Done")(done))
  }

  override def onGuiClosed(): Unit = {
    Keyboard.enableRepeatEvents(false)
  }


  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    drawString(fontRenderer, "Enter preset json:", 10, 10, 0xffffffff)
    textArea.drawTextBox()
  }

  override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
    super.mouseClicked(mouseX, mouseY, mouseButton)
    textArea.mouseClicked(mouseX, mouseY, mouseButton)
  }

  override def keyTyped(typedChar: Char, keyCode: Int): Unit = {
    super.keyTyped(typedChar, keyCode)
    textArea.textboxKeyTyped(typedChar, keyCode)
  }

  def beautify(): Unit = {
    val gson = new GsonBuilder().setPrettyPrinting().create()
    Try(new JsonParser().parse(textArea.getText))
      .map(gson.toJson)
      .foreach(textArea.setText)
  }
}

object GuiImportPreset {
  def export(parent: GuiSetupDimensionalLayersPreset): () => Unit = () => {
    GuiScreen.setClipboardString(parent.layersList.toSettings)
  }
}
