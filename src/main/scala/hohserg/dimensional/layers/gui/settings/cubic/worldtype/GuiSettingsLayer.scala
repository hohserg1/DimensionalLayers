package hohserg.dimensional.layers.gui.settings.cubic.worldtype

import hohserg.dimensional.layers.gui.DrawableArea.Container
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.GuiSelectDimension.DrawableDim
import hohserg.dimensional.layers.gui.GuiTileList.SelectHandler
import hohserg.dimensional.layers.gui.IconUtils._
import hohserg.dimensional.layers.gui.RelativeCoord.{alignLeft, alignTop}
import hohserg.dimensional.layers.gui._
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.cubic.worldtype.GuiSettingsLayer.dimensionTypeArea
import hohserg.dimensional.layers.gui.settings.{GuiBaseSettingsLayer, GuiFakeCreateWorld}
import hohserg.dimensional.layers.preset.{CubicWorldTypeLayerSpec, LayerSpec}
import net.minecraft.client.resources.I18n
import net.minecraft.world.DimensionType
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import java.awt.Rectangle
import scala.util.Try

@SideOnly(Side.CLIENT)
class GuiSettingsLayer(parent: GuiSetupDimensionalLayersPreset, val layer: CubicWorldTypeLayerSpec, index: Int)
  extends GuiBaseSettingsLayer(parent, index)
    with SelectHandler[DrawableDim]
    with Container {
  val seedOverrideH = new ValueHolder[String](layer.seedOverride.map(_.toString).getOrElse(""))
  val worldTypePresetH = new ValueHolder[String](layer.worldTypePreset)
  val dimensionTypeH = new ValueHolder[DimensionType](layer.dimensionType1)

  override def buildLayerSpec(): LayerSpec =
    CubicWorldTypeLayerSpec(
      layer.cubicWorldType,
      worldTypePresetH.get,
      dimensionTypeH.get,
      Some(seedOverrideH.get).filter(_.nonEmpty).map(toLongSeed)
    )

  private val guiFakeCreateWorld = new GuiFakeCreateWorld(this, layer.worldTypePreset)

  def toLongSeed(str: String): Long =
    Try(str.toLong).filter(_ != 0).getOrElse(str.hashCode.toLong)

  override def initGui(): Unit = {
    super.initGui()

    val seedOverrideField = addElement(new GuiTextFieldElement(width - 180, height / 2 - 20 - 20, 170, 20, seedOverrideH, identity))
    addCenteredLabel("seed override:", alignLeft(seedOverrideField.x + seedOverrideField.width / 2), alignTop(seedOverrideField.y - 13), 0xffa0a0a0)

    addLabel(makeWorldTypeLabel(layer.cubicWorldType), 10, 10 + IconUtils.width + 10, 0xffffffff)
    addLabel(makeDimensionTypeLabel(dimensionTypeH.get), dimensionTypeArea.x, dimensionTypeArea.y2 + 10, 0xffffffff)

    addButton(new GuiClickableButton(width - 150 - 10, height / 2 - 5 + 20 + 1, 150, 20, I18n.format("selectWorld.customizeType"))(() => {
      layer.cubicWorldType.onCustomizeButton(mc, guiFakeCreateWorld)
    }) {
      visible = layer.cubicWorldType.isCustomizable
    })
  }


  override def drawScreenPost(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreenPost(mouseX, mouseY, partialTicks)
    drawLogo(layer.cubicWorldType, 10, 10)
    drawLogo(dimensionTypeH.get, dimensionTypeArea.x, dimensionTypeArea.y)
    if (dimensionTypeArea.isHovering) {
      drawHighlightHovering(dimensionTypeArea)
    }
  }

  override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
    super.mouseClicked(mouseX, mouseY, mouseButton)
    if (dimensionTypeArea.isHovering) {
      show(new GuiSelectCompatibleDimension(_))()
    }
  }

  override def onSelected(item: DrawableDim): Unit = {
    dimensionTypeH.set(item.dimensionType)
  }

  override def minX: Int = 0

  override def minY: Int = 0

  override def maxX: Int = width

  override def maxY: Int = height
}

@SideOnly(Side.CLIENT)
object GuiSettingsLayer {
  val dimensionTypeArea = DrawableArea(
    new Rectangle(10, 10 + IconUtils.width + 10 + 10 + 10, IconUtils.width, IconUtils.width),
    new Rectangle(0, 0, 256, 256),
    new Rectangle(0, 0, 256, 256)
  )
}
