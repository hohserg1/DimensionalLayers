package hohserg.dimensional.layers.gui.settings.dimension

import hohserg.dimensional.layers.DimensionalLayersPreset.DimensionLayerSpec
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.RelativeCoord.{alignLeft, alignTop}
import hohserg.dimensional.layers.gui._
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer
import hohserg.dimensional.layers.gui.settings.dimension.GuiSettingsLayer._
import hohserg.dimensional.layers.{DimensionalLayersPreset, DimensionalLayersWorldType, Main, clamp}
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

  class CyclicValueHolder[A](init: A, possible: Seq[A])(implicit gui: GuiBaseSettings)
    extends ValueHolder[Int](possible.indexOf(init), _ % possible.size) {

    def getA: A = possible(get)

    def next(): A = {
      set(get + 1)
      getA
    }
  }

}

class GuiSettingsLayer(parent: GuiSetupDimensionalLayersPreset, index: Int, layer: DimensionLayerSpec) extends GuiBaseSettingsLayer(parent, index) {
  val seedOverrideH = new ValueHolder[String](layer.seedOverride.map(_.toString).getOrElse(""))
  val topOffset: ValueHolder[Int] = new ValueHolder[Int](layer.topOffset, clamp(_, 0, 15 - bottomOffset.get))
  val bottomOffset: ValueHolder[Int] = new ValueHolder[Int](layer.bottomOffset, clamp(_, 0, 15 - topOffset.get))
  val worldTypeH = new CyclicValueHolder[WorldType](layer.worldType, possibleWorldTypes)
  val worldTypePresetH = new ValueHolder[String](layer.worldTypePreset)

  override def buildLayerSpec(): DimensionalLayersPreset.LayerSpec =
    DimensionLayerSpec(
      layer.dimensionType,
      Some(seedOverrideH.get).filter(_.nonEmpty).map(toLongSeed),
      topOffset.get,
      bottomOffset.get,
      worldTypeH.getA,
      worldTypePresetH.get
    )

  var seedOverrideField: GuiTextFieldElement[String] = _
  var topOffsetField: GuiOffsetField = _
  var bottomOffsetField: GuiOffsetField = _
  var worldTypeButton: GuiClickableButton = _
  var worldTypeCustomizationButton: GuiClickableButton = _

  private val guiFakeCreateWorld = new GuiFakeCreateWorld(this, layer.worldTypePreset)

  override def initGui(): Unit = {
    super.initGui()

    seedOverrideField = addElement(new GuiTextFieldElement(width - 180, height / 2 - 20 - 20, 170, 20, seedOverrideH, identity))
    addCenteredLabel("seed override:", alignLeft(seedOverrideField.x + seedOverrideField.width / 2), alignTop(seedOverrideField.y - 13), 0xffa0a0a0)

    addLabel(DimensionClientUtils.getDisplayName(layer.dimensionType), 10, DimensionClientUtils.width + 10 * 2, 0xffffffff)

    topOffsetField = addElement(new GuiOffsetField(gridTop, topOffset, null))
    bottomOffsetField = addElement(new GuiOffsetField(gridTop, bottomOffset, topOffsetField))


    worldTypeCustomizationButton = addButton(new GuiClickableButton(width - 150 - 10, height / 2 - 5 + 20 + 1, 150, 20, I18n.format("selectWorld.customizeType"))(() => {
      worldTypeH.getA.onCustomizeButton(mc, guiFakeCreateWorld)
    }))
    worldTypeButton = addButton(new GuiClickableButton(width - 150 - 10, height / 2 - 5, 150, 20, makeWorldTypeLabel(worldTypeH.getA))(() => {
      val worldType = worldTypeH.next()
      worldTypeButton.displayString = makeWorldTypeLabel(worldType)
      worldTypeCustomizationButton.visible = worldType.isCustomizable
      guiFakeCreateWorld.chunkProviderSettingsJson = ""
    }))
  }

  def makeWorldTypeLabel(worldType: WorldType): String =
    I18n.format("selectWorld.mapType") + " " + I18n.format(worldType.getTranslationKey)

  def toLongSeed(str: String): Long =
    Try(str.toLong).filter(_ != 0).getOrElse(str.hashCode.toLong)

  override def drawScreenPre(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreenPre(mouseX, mouseY, partialTicks)
    DimensionClientUtils.drawLogo(layer.dimensionType, 10, 10)
    drawLayerGrid(mouseX, mouseY)
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
}
