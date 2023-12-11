package hohserg.dimensional.layers.gui.settings.dimension

import hohserg.dimensional.layers.DimensionalLayersPreset.DimensionLayerSpec
import hohserg.dimensional.layers.gui.GuiNumericField.NumberHolder
import hohserg.dimensional.layers.gui.RelativeCoord.{alignLeft, alignTop}
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer
import hohserg.dimensional.layers.gui.settings.dimension.GuiSettingsLayer._
import hohserg.dimensional.layers.gui.{DimensionClientUtils, GuiClickableButton, GuiTextFieldElement}
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

  lazy val possibleWorldTypes =
    WorldType.WORLD_TYPES
      .filter(_ != null)
      .filter(_.canBeCreated)
      .filter(_ != DimensionalLayersWorldType)

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
  private var worldTypeIndex = possibleWorldTypes.indexOf(layer.worldType)
  private val guiFakeCreateWorld = new GuiFakeCreateWorld(this, layer.worldTypePreset)

  override def initGui(): Unit = {
    super.initGui()

    addFreeDrawable(() => DimensionClientUtils.drawLogo(layer.dimensionType, 10, 10))
    addFreeDrawable(() => drawLayerGrid())

    seedOverrideField = addElement(new GuiTextFieldElement(2, width - 180, height / 2 - 20 - 20, 170, 20, layer.seedOverride.map(_.toString).getOrElse("")))
    seedOverrideField.setMaxStringLength(32)

    addCenteredLabel("seed override:", alignLeft(seedOverrideField.x + seedOverrideField.width / 2), alignTop(seedOverrideField.y - 13), 0xffa0a0a0)
    addLabel(DimensionClientUtils.getDisplayName(layer.dimensionType), alignLeft(10), alignTop(DimensionClientUtils.width + 10 * 2), 0xffffffff)

    topOffsetField = addElement(new GuiOffsetField(3, gridTop, topOffset, layer.topOffset, null))
    bottomOffsetField = addElement(new GuiOffsetField(4, gridTop, bottomOffset, layer.bottomOffset, topOffsetField))

    worldTypeButton = addButton(new GuiClickableButton(5, width - 150 - 10, height / 2 - 5, 150, 20, makeWorldTypeLabel(currentWorldType))(() => {
      val worldType = nextWorldType()

      worldTypeButton.displayString = makeWorldTypeLabel(worldType)
      markChanged()
      worldTypeCustomizationButton.visible = worldType.isCustomizable
      guiFakeCreateWorld.chunkProviderSettingsJson = ""
    }))

    worldTypeCustomizationButton = addButton(new GuiClickableButton(6, width - 150 - 10, height / 2 - 5 + 20 + 1, 150, 20, I18n.format("selectWorld.customizeType"))(() => {
      currentWorldType.onCustomizeButton(mc, guiFakeCreateWorld)
    }) {
      visible = currentWorldType.isCustomizable
    })
  }

  private def nextWorldType() = {
    worldTypeIndex += 1
    if (worldTypeIndex >= possibleWorldTypes.length)
      worldTypeIndex = 0

    currentWorldType
  }

  private def currentWorldType = {
    possibleWorldTypes(worldTypeIndex)
  }

  def makeWorldTypeLabel(worldType: WorldType): String =
    I18n.format("selectWorld.mapType") + " " + I18n.format(worldType.getTranslationKey)

  def toLongSeed(str: String): Long =
    Try(str.toLong).filter(_ != 0).getOrElse(str.hashCode.toLong)

  def gridTop = height / 2 - 209 / 2

  def drawLayerGrid(): Unit = {
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
}
