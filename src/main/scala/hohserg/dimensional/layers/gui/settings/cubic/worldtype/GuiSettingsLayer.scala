package hohserg.dimensional.layers.gui.settings.cubic.worldtype

import hohserg.dimensional.layers.data.LayerMap
import hohserg.dimensional.layers.gui.DrawableArea.Container
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.GuiSelectDimension.DrawableDim
import hohserg.dimensional.layers.gui.GuiTileList.SelectHandler
import hohserg.dimensional.layers.gui.IconUtils._
import hohserg.dimensional.layers.gui.RelativeCoord.{alignLeft, alignTop}
import hohserg.dimensional.layers.gui._
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer.texture
import hohserg.dimensional.layers.gui.settings.cubic.worldtype.GuiSettingsLayer.dimensionTypeArea
import hohserg.dimensional.layers.gui.settings.{GuiBaseSettingsLayer, GuiFakeCreateWorld}
import hohserg.dimensional.layers.preset.spec.{CubicWorldTypeLayerSpec, LayerSpec}
import hohserg.dimensional.layers.{clamp, toLongSeed}
import net.minecraft.client.resources.I18n
import net.minecraft.world.DimensionType
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import java.awt.Rectangle

@SideOnly(Side.CLIENT)
class GuiSettingsLayer(parent: GuiSetupDimensionalLayersPreset, val layer: CubicWorldTypeLayerSpec, index: Int)
  extends GuiBaseSettingsLayer(parent, index)
    with SelectHandler[DrawableDim]
    with Container {
  val seedOverrideH = new ValueHolder[String](layer.seedOverride.map(_.toString).getOrElse(""))
  val worldTypePresetH = new ValueHolder[String](layer.worldTypePreset)
  val dimensionTypeH = new ValueHolder[DimensionType](layer.dimensionType1)
  val minCubeY: ValueHolder[Int] = new ValueHolder[Int](layer.minCubeY, clamp(_, LayerMap.minCubeY, math.min(LayerMap.maxCubeY, maxCubeY.get)))
  val maxCubeY: ValueHolder[Int] = new ValueHolder[Int](layer.maxCubeY, clamp(_, math.max(LayerMap.minCubeY, minCubeY.get), LayerMap.maxCubeY))

  override def buildLayerSpec(): LayerSpec =
    CubicWorldTypeLayerSpec(
      layer.cubicWorldType,
      worldTypePresetH.get,
      dimensionTypeH.get,
      minCubeY.get,
      maxCubeY.get,
      toLongSeed(seedOverrideH.get)
    )

  var maxCubeYField: GuiBoundField = _
  var minCubeYField: GuiBoundField = _

  private val guiFakeCreateWorld = new GuiFakeCreateWorld(this, layer.worldTypePreset)

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

    gridLeft = (IconUtils.width + 10 * 2 + width - 180) / 2

    maxCubeYField = addElement(new GuiBoundField(gridLeft + 19, maxCubeY, true))
    minCubeYField = addElement(new GuiBoundField(gridLeft + 19, minCubeY, false))
  }

  override def drawScreenPre(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreenPre(mouseX, mouseY, partialTicks)
    drawLogo(layer.cubicWorldType, 10, 10)
    drawLogo(dimensionTypeH.get, dimensionTypeArea.x, dimensionTypeArea.y)
    if (dimensionTypeArea.isHovering) {
      drawHighlightHovering(dimensionTypeArea)
    }
    drawLayerGrid()
  }

  var gridLeft = IconUtils.width + 120
  val gridTop = 10
  val gridCellSize = 13

  def drawLayerGrid(): Unit = {
    mc.getTextureManager.bindTexture(texture)

    val Seq(bottom, mid, top) = Seq(minCubeY.get, 0, maxCubeY.get).sorted

    var enabledStarted = false
    var zeroCell = -10
    var maxCell = -10
    var minCell = -10

    for (y <- 0 until 3) {
      drawEmptyCell(y)
      drawDisabledCell(y)
    }

    if (top == 0) {
      zeroCell = 3
    }

    if (top == maxCubeY.get) {
      enabledStarted = true
      maxCubeYField.y = gridTop + 3 * gridCellSize - 3 + 1
      maxCell = 3
    }

    val lastUsedY1 = drawGridSegment(3, top, mid, enabledStarted, 5)

    if (mid == minCubeY.get) {
      enabledStarted = false
      minCubeYField.y = gridTop + lastUsedY1 * gridCellSize - 3 - 1 + gridCellSize
      minCell = lastUsedY1
      if (minCubeY.get == maxCubeY.get) {
        maxCubeYField.y = gridTop + lastUsedY1 * gridCellSize - 3 + 1
        maxCell = lastUsedY1
      }

    } else if (mid == maxCubeY.get) {
      enabledStarted = true
      maxCubeYField.y = gridTop + lastUsedY1 * gridCellSize - 3 + 1
      maxCell = lastUsedY1
    }

    if (mid == 0)
      zeroCell = lastUsedY1

    val lastUsedY2 = if (true) drawGridSegment(lastUsedY1, mid, bottom, enabledStarted, 5) else 100

    if (bottom == minCubeY.get) {
      minCubeYField.y = gridTop + lastUsedY2 * gridCellSize - 3 - 1 + gridCellSize
      minCell = lastUsedY2
    }

    if (bottom == 0)
      zeroCell = lastUsedY2

    for (y <- lastUsedY2 to lastUsedY2 + 2) {
      drawEmptyCell(y)
      drawDisabledCell(y)
    }

    drawEnabledCell(maxCell)
    drawEnabledCell(minCell)
    drawZeroCell(zeroCell)
  }

  def drawGridSegment(yStart: Int, top: Int, bottom: Int, enabled: Boolean, len: Int): Int = {
    if (top - bottom >= len) {
      for (y <- yStart until (yStart + len / 2)) {
        drawEmptyCell(y)
        if (enabled)
          drawEnabledCell(y)
        else
          drawDisabledCell(y)
      }
      drawPoints(yStart + len / 2)
      for (y <- (yStart + len / 2 + 1) until (yStart + len)) {
        drawEmptyCell(y)
        if (enabled)
          drawEnabledCell(y)
        else
          drawDisabledCell(y)
      }
      yStart + len - 1
    } else {
      for (y <- yStart to (yStart + (top - bottom))) {
        drawEmptyCell(y)
        if (enabled)
          drawEnabledCell(y)
        else
          drawDisabledCell(y)
      }
      yStart + (top - bottom)
    }
  }

  def drawEmptyCell(y: Int): Unit = {
    drawTexturedModalRect(gridLeft, gridTop + y * gridCellSize + 1, 0, 0, 14, 14)
  }

  def drawEnabledCell(y: Int): Unit = {
    drawTexturedModalRect(gridLeft + 1, gridTop + 1 + y * gridCellSize + 1, 15, 1, 12, 12)
  }

  def drawDisabledCell(y: Int): Unit = {
    drawTexturedModalRect(gridLeft + 1, gridTop + 1 + y * gridCellSize + 1, 15, 14, 12, 12)
  }

  def drawZeroCell(y: Int): Unit = {
    drawTexturedModalRect(gridLeft + 1, gridTop + 1 + y * gridCellSize + 1, 15, 27, 12, 12)
  }

  def drawPoints(y: Int): Unit = {
    drawTexturedModalRect(gridLeft + 1, gridTop + 1 + y * gridCellSize + 1, 15, 40, 12, 12)
  }

  override def drawScreenPost(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreenPost(mouseX, mouseY, partialTicks)
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
