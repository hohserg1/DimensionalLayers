package hohserg.dimensional.layers.gui.preset

import com.google.gson.{GsonBuilder, JsonParser}
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.{GuiBaseSettings, GuiClickableButton}
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard

import scala.util.Try

class GuiImportPreset(parent: GuiSetupDimensionalLayersPreset) extends GuiBaseSettings(parent) {
  val presetJson = new ValueHolder[String](parent.layersList.toSettings)

  override def done(): Unit = {
    parent.initFromJson(presetJson.get)
    back()
  }

  override def initGui(): Unit = {
    super.initGui()

    val textArea = addElement(new GuiMultiLineTextFieldElement(10, 10 + 15, width - 10 - 10 - 80 - 10, height - 10 - 15 - 10, presetJson))
    Keyboard.enableRepeatEvents(true)
    textArea.setMaxStringLength(1000000)
    textArea.setCanLoseFocus(false)
    textArea.setFocused(true)
    textArea.setEnableBackgroundDrawing(true)

    addButton(new GuiClickableButton(width - 80 - 10, height / 2 - 10, 80, 20, "Beautify")(beautify))

    addLabel("Enter preset json:", 10, 10, 0xffffffff)
  }

  override def onGuiClosed(): Unit = {
    Keyboard.enableRepeatEvents(false)
  }

  def beautify(): Unit = {
    val gson = new GsonBuilder().setPrettyPrinting().create()
    Try(new JsonParser().parse(presetJson.get))
      .map(gson.toJson)
      .foreach(presetJson.set)
  }
}

object GuiImportPreset {
  def export(parent: GuiSetupDimensionalLayersPreset): () => Unit = () => {
    GuiScreen.setClipboardString(parent.layersList.toSettings)
    parent.exportButton.displayString = "Copied!"
  }
}
