package hohserg.dimensional.layers.gui.settings.dimension

import hohserg.dimensional.layers.gui.*
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.IconUtils.*
import hohserg.dimensional.layers.gui.RelativeCoord.{alignLeft, alignTop}
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer.*
import hohserg.dimensional.layers.gui.settings.additional.features.addAdditionalFeaturesWidgets
import hohserg.dimensional.layers.gui.settings.base.offsets.addOffsetsWidgets
import hohserg.dimensional.layers.gui.settings.dimension.GuiSettingsLayer.{CyclicValueHolder, possibleWorldTypes}
import hohserg.dimensional.layers.gui.settings.{GuiBaseSettingsLayer, GuiFakeCreateWorld}
import hohserg.dimensional.layers.preset.spec.{AdditionalFeature, CubeOffsets, DimensionLayerSpec, LayerSpec}
import hohserg.dimensional.layers.{clamp, toLongSeed}
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import net.minecraft.client.resources.I18n
import net.minecraft.world.WorldType
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
object GuiSettingsLayer {

  lazy val possibleWorldTypes =
    WorldType.WORLD_TYPES
             .filter(_ != null)
             .filter(_.canBeCreated)
             .filter(!_.isInstanceOf[ICubicWorldType])
             .filter(_.getName != "OTG")
             .toSeq

  class CyclicValueHolder[A](init: A, possible: Seq[A])(implicit gui: GuiBaseSettings)
    extends ValueHolder[Int](possible.indexOf(init), _ % possible.size) {

    def getA: A = possible(get)

    def next(): A = {
      set(get + 1)
      getA
    }
  }

}

@SideOnly(Side.CLIENT)
class GuiSettingsLayer(parent: GuiSetupDimensionalLayersPreset, index: Int, layer: DimensionLayerSpec) extends GuiBaseSettingsLayer(parent, index) {
  val seedOverrideH = new ValueHolder[String](layer.seedOverride.map(_.toString).getOrElse(""))
  val topOffset: ValueHolder[Int] = new ValueHolder[Int](layer.offsets.topOffset, clamp(_, 0, 15 - bottomOffset.get))
  val bottomOffset: ValueHolder[Int] = new ValueHolder[Int](layer.offsets.bottomOffset, clamp(_, 0, 15 - topOffset.get))
  val worldTypeH = new CyclicValueHolder[WorldType](layer.worldType, possibleWorldTypes)
  val worldTypePresetH = new ValueHolder[String](layer.worldTypePreset)
  val additionalFeaturesH = new ValueHolder[Seq[AdditionalFeature]](layer.additionalFeatures)

  override def buildLayerSpec(): LayerSpec = {
    DimensionLayerSpec(
      layer.dimensionType,
      toLongSeed(seedOverrideH.get),
      CubeOffsets(
        topOffset.get,
        bottomOffset.get
      ),
      worldTypeH.getA,
      worldTypePresetH.get,
      additionalFeaturesH.get
    )
  }

  var worldTypeButton: GuiClickableButton = null
  var worldTypeCustomizationButton: GuiClickableButton = null

  private val guiFakeCreateWorld = new GuiFakeCreateWorld(this, worldTypePresetH, layer.worldTypePreset)

  override def initGui(): Unit = {
    super.initGui()

    val seedOverrideField = addElement(new GuiTextFieldElement(width - 180, height / 2 - 20 - 20, 170, 20, seedOverrideH, identity))
    addCenteredLabel("seed override:", alignLeft(seedOverrideField.x + seedOverrideField.width / 2), alignTop(seedOverrideField.y - 13), 0xffa0a0a0)

    val dimNameLabel = makeDimensionTypeLabel(layer.dimensionType)
    val dimNameLabelX = math.max(10, 10 + IconUtils.width / 2 - fontRenderer.getStringWidth(dimNameLabel) / 2)
    addLabel(dimNameLabel, dimNameLabelX, IconUtils.width + 10 * 2, 0xffffffff)

    val offset = 70 + 10
    addOffsetsWidgets(topOffset, bottomOffset, left = IconUtils.width + 10 * 2 + offset, right = seedOverrideField.x - offset)

    worldTypeCustomizationButton = addButton(new GuiClickableButton(width - 150 - 10, height / 2 - 5 + 20 + 1, 150, 20, I18n.format("selectWorld.customizeType"))(() => {
      worldTypeH.getA.onCustomizeButton(mc, guiFakeCreateWorld)
    }) {
      visible = worldTypeH.getA.isCustomizable
    })
    worldTypeButton = addButton(new GuiClickableButton(width - 150 - 10, height / 2 - 5, 150, 20, makeWorldTypeLabel(worldTypeH.getA))(() => {
      val worldType = worldTypeH.next()
      worldTypeButton.displayString = makeWorldTypeLabel(worldType)
      worldTypeCustomizationButton.visible = worldType.isCustomizable
      guiFakeCreateWorld.chunkProviderSettingsJson = ""
    }))

    addAdditionalFeaturesWidgets(additionalFeaturesH, top = math.max(IconUtils.width + 10 * 6 + 9, height - 100), bottom = height - 10)
  }

  override def drawScreenPre(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreenPre(mouseX, mouseY, partialTicks)
    drawLogo(layer.dimensionType, 10, 10)
  }
}
