package hohserg.dimensional.layers.gui.settings.dimension

import hohserg.dimensional.layers.DimensionalLayersPreset.DimensionLayerSpec
import hohserg.dimensional.layers.gui.GuiNumericField.NumberHolder
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer
import hohserg.dimensional.layers.gui.settings.dimension.GuiSettingsLayer._
import hohserg.dimensional.layers.gui.{DimensionClientUtils, GuiClickableButton}
import hohserg.dimensional.layers.{DimensionalLayersPreset, DimensionalLayersWorldType, Main, clamp}
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation
import net.minecraft.world.WorldType

import scala.util.Try

object GuiSettingsLayer {
  val texture = new ResourceLocation(Main.modid, "textures/gui/dimension_settings.png")
  val gridCellSize = 13
  val gridLeft = DimensionClientUtils.width + 10 * 2 + 70

}

class GuiSettingsLayer(parent: GuiSetupDimensionalLayersPreset, index: Int, layer: DimensionLayerSpec) extends GuiBaseSettingsLayer(parent, layer, index) {
  override def buildLayerSpec(): DimensionalLayersPreset.LayerSpec =
    DimensionLayerSpec(
      layer.dimensionType,
      Some(seedOverrideField.getText).filter(_.nonEmpty).map(toLongSeed),
      topOffset.get,
      bottomOffset.get,
      currentWorldType,
      guiFakeCreateWorld.chunkProviderSettingsJson
    )

  val topOffset: NumberHolder[Int] = new NumberHolder[Int](layer.topOffset) {
    override def validate(v: Int): Int = clamp(v, 0, 15 - bottomOffset.get)
  }
  val bottomOffset: NumberHolder[Int] = new NumberHolder[Int](layer.bottomOffset) {
    override def validate(v: Int): Int = clamp(v, 0, 15 - topOffset.get)
  }

  var seedOverrideField: GuiTextField = _
  var topOffsetField: GuiOffsetField = _
  var bottomOffsetField: GuiOffsetField = _
  var worldTypeButton: GuiClickableButton = _
  var worldTypeCustomizationButton: GuiClickableButton = _
  private var worldTypeIndex = WorldType.WORLD_TYPES.indexOf(layer.worldType)
  private val guiFakeCreateWorld = new GuiFakeCreateWorld(this, layer.worldTypePreset)

  override def initGui(): Unit = {
    super.initGui()

    seedOverrideField = new GuiTextField(2, fontRenderer, width - 180, height / 2 - 20 - 20, 170, 20)
    layer.seedOverride.map(_.toString).foreach(seedOverrideField.setText)

    topOffsetField = new GuiOffsetField(3, gridTop, topOffset, true)
    bottomOffsetField = new GuiOffsetField(4, gridTop, bottomOffset, false)

    worldTypeButton = addButton(new GuiClickableButton(5, width - 150 - 10, height / 2 - 5, 150, 20, makeWorldTypeLabel(layer.worldType))(() => {
      var worldType = nextWorldType()
      if (worldType == DimensionalLayersWorldType) {
        worldType = nextWorldType()
      }

      worldTypeButton.displayString = makeWorldTypeLabel(worldType)
      markChanged()
      worldTypeCustomizationButton.visible = worldType.isCustomizable
      guiFakeCreateWorld.chunkProviderSettingsJson = ""
    }))

    worldTypeCustomizationButton = addButton(new GuiClickableButton(6, width - 150 - 10, height / 2 + 20 + 1, 150, 20, I18n.format("selectWorld.customizeType"))(() => {
      currentWorldType.onCustomizeButton(mc, guiFakeCreateWorld)
    }) {
      visible = currentWorldType.isCustomizable
    })
  }

  private def nextWorldType() = {
    worldTypeIndex += 1
    if (worldTypeIndex >= WorldType.WORLD_TYPES.length || currentWorldType == null) {
      worldTypeIndex = 0
    }
    currentWorldType
  }

  private def currentWorldType = {
    WorldType.WORLD_TYPES(worldTypeIndex)
  }

  def makeWorldTypeLabel(worldType: WorldType): String =
    I18n.format("selectWorld.mapType") + " " + I18n.format(worldType.getTranslationKey)

  def toLongSeed(str: String): Long =
    Try(str.toLong).filter(_ != 0).getOrElse(str.hashCode.toLong)

  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    drawDefaultBackground()
    DimensionClientUtils.drawLogo(layer.dimensionType, 10, 10)
    drawLayerGrid(mouseX, mouseY)
    super.drawScreen(mouseX, mouseY, partialTicks)

    drawCenteredString(fontRenderer, "seed override:", seedOverrideField.x + seedOverrideField.width / 2, seedOverrideField.y - 13, 0xffa0a0a0)
    drawString(fontRenderer, DimensionClientUtils.getDisplayName(layer.dimensionType), 10, DimensionClientUtils.width + 10 * 2, 0xffffffff)

    seedOverrideField.drawTextBox()
    topOffsetField.drawTextBox()
    bottomOffsetField.drawTextBox()
  }

  def gridTop = height / 2 - 209 / 2

  def drawLayerGrid(mouseX: Int, mouseY: Int): Unit = {
    val firstEnabled = 0 + topOffset.get
    val lastEnabled = 15 - bottomOffset.get

    mc.getTextureManager.bindTexture(texture)

    drawTexturedModalRect(gridLeft, gridTop, 0, 0, 14, 209)

    for {
      i <- 0 until firstEnabled
    } drawDisabledCell(i)

    for {
      i <- firstEnabled to lastEnabled
    } drawEnabledCell(i)

    for {
      i <- 15 until lastEnabled by -1
    } drawDisabledCell(i)

  }

  def drawDisabledCell(i: Int): Unit = {
    drawTexturedModalRect(gridLeft + 1, gridTop + i * gridCellSize + 1, 15, 14, 12, 12)
  }

  def drawEnabledCell(i: Int): Unit = {
    drawTexturedModalRect(gridLeft + 1, gridTop + i * gridCellSize + 1, 15, 1, 12, 12)
  }

  override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
    super.mouseClicked(mouseX, mouseY, mouseButton)
    seedOverrideField.mouseClicked(mouseX, mouseY, mouseButton)

    if (!topOffsetField.mouseClicked(mouseX, mouseY, mouseButton))
      bottomOffsetField.mouseClicked(mouseX, mouseY, mouseButton)
  }

  override def mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long): Unit = {
    super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
    topOffsetField.mouseClickMove(mouseX, mouseY)
    bottomOffsetField.mouseClickMove(mouseX, mouseY)
  }

  override def mouseReleased(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
    super.mouseReleased(mouseX, mouseY, mouseButton)
    topOffsetField.mouseReleased(mouseX, mouseY, mouseButton)
    bottomOffsetField.mouseReleased(mouseX, mouseY, mouseButton)
    if (topOffset.get != layer.topOffset || bottomOffset.get != layer.bottomOffset)
      markChanged()
  }

  override def keyTyped(typedChar: Char, keyCode: Int): Unit = {
    super.keyTyped(typedChar, keyCode)
    seedOverrideField.textboxKeyTyped(typedChar, keyCode)
    topOffsetField.textboxKeyTyped(typedChar, keyCode)
    bottomOffsetField.textboxKeyTyped(typedChar, keyCode)
    if (layer.seedOverride.isEmpty && seedOverrideField.getText.nonEmpty || layer.seedOverride.nonEmpty && layer.seedOverride.get.toString != seedOverrideField.getText)
      markChanged()
  }
}
