package hohserg.dimensional.layers.gui.preset

import com.google.gson.{GsonBuilder, JsonParser}
import hohserg.dimensional.layers.gui.RelativeCoord.{alignLeft, alignTop}
import hohserg.dimensional.layers.gui.{GuiBaseSettingsButtons, GuiClickableButton}
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard

import scala.util.Try

class GuiImportPreset(parent: GuiSetupDimensionalLayersPreset) extends GuiBaseSettingsButtons(parent) {
  var textArea: GuiMultiLineTextFieldElement = _

  override def done(): Unit = {
    parent.initFromJson(textArea.getText)
    super.done()
  }


  override def initGui(): Unit = {
    super.initGui()
    val prevTest = if (textArea == null) parent.layersList.toSettings else textArea.getText

    textArea = addElement(new GuiMultiLineTextFieldElement(0, 10, 10 + 15, width - 10 - 10 - 80 - 10, height - 10 - 15 - 10, prevTest))
    Keyboard.enableRepeatEvents(true)
    textArea.setMaxStringLength(10000)
    textArea.setText(prevTest)
    textArea.setCanLoseFocus(false)
    textArea.setFocused(true)
    textArea.setEnableBackgroundDrawing(true)

    addLabel("Enter preset json:", alignLeft(10), alignTop(10), 0xffffffff)

    addButton(new GuiClickableButton(1, width - 80 - 10, height / 2 - 10, 80, 20, "Beautify")(beautify))
  }

  override def onGuiClosed(): Unit = {
    Keyboard.enableRepeatEvents(false)
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
