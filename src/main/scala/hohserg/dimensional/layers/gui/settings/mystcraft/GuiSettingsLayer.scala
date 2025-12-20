package hohserg.dimensional.layers.gui.settings.mystcraft

import com.google.common.cache.LoadingCache
import com.xcompwiz.mystcraft.client.gui.GuiUtils
import com.xcompwiz.mystcraft.symbol.SymbolManager
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.RelativeCoord.{alignLeft, alignTop}
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer
import hohserg.dimensional.layers.gui.settings.additional.features.addAdditionalFeaturesWidgets
import hohserg.dimensional.layers.gui.settings.base.offsets.addOffsetsWidgets
import hohserg.dimensional.layers.gui.{GuiNumericField, GuiTextFieldElement, GuiTexturedButton, GuiTileList, IconUtils}
import hohserg.dimensional.layers.preset.spec.{AdditionalFeature, CubeOffsets, LayerSpec, MystcraftLayerSpec}
import hohserg.dimensional.layers.{clamp, toLongSeed}
import net.minecraft.client.Minecraft

import java.awt.Rectangle
import scala.jdk.CollectionConverters.*




val symbolLinesByLen: LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[SymbolDrawable]]] =
  GuiTileList.createLinesCache(SymbolManager.getAgeSymbols.asScala.toSeq.map(_.getRegistryName.toString).map(SymbolDrawable(_)), 16)

class GuiSettingsLayer(parent: GuiSetupDimensionalLayersPreset, layer: MystcraftLayerSpec, index: Int)
  extends GuiBaseSettingsLayer(parent, index)
    with GuiTileList.SelectHandler[SymbolDrawable] {

  val seedOverrideH = new ValueHolder[String](layer.seedOverride.map(_.toString).getOrElse(""))
  val instabilityH = new ValueHolder[Short](layer.instability, math.max(_, 0).toShort)
  val symbolsH = new ValueHolder[Seq[String]](layer.symbols)
  val topOffset: ValueHolder[Int] = new ValueHolder[Int](layer.offsets.topOffset, clamp(_, 0, 15 - bottomOffset.get))
  val bottomOffset: ValueHolder[Int] = new ValueHolder[Int](layer.offsets.bottomOffset, clamp(_, 0, 15 - topOffset.get))
  val additionalFeaturesH = new ValueHolder[Seq[AdditionalFeature]](layer.additionalFeatures)

  var currentSymbolCategory = SymbolCategory.color
  var tabsBackLeft = 0
  var tabsBackTop = 0
  var tabsBackRight = 0
  var tabsBackBottom = 0

  override def buildLayerSpec(): LayerSpec =
    MystcraftLayerSpec(
      toLongSeed(seedOverrideH.get),
      instabilityH.get,
      symbolsH.get,
      CubeOffsets(
        topOffset.get,
        bottomOffset.get
      ),
      additionalFeaturesH.get
    )

  override def initGui(): Unit = {
    super.initGui()

    val seedOverrideField = addElement(new GuiTextFieldElement(10, height / 2 - 20 - 20, 78, 20, seedOverrideH, identity))
    addCenteredLabel("seed override:", alignLeft(seedOverrideField.x + seedOverrideField.width / 2), alignTop(seedOverrideField.y - 13), 0xffa0a0a0)

    val offset = 70 + 10
    val offsetsLeft = IconUtils.width + 10 * 3 + offset
    val offsetsRight = offsetsLeft + 10 + offset
    addOffsetsWidgets(topOffset, bottomOffset, left = offsetsLeft, right = offsetsRight - offset)

    addAdditionalFeaturesWidgets(additionalFeaturesH, top = math.max(IconUtils.width + 10 * 6 + 9, height - 100), bottom = height - 10)

    val instabilityField = addElement(new GuiNumericField(
      x = 10 + fontRenderer.getStringWidth("instability:") + 3,
      y = 10,
      maxLen = 3,
      value = instabilityH,
      fromString = _.toShort
    ))
    addLabel("instability:", alignLeft(10), alignTop(instabilityField.y + 5), 0xffa0a0a0)

    val usedSymbolsList = addElement(new UsedSymbolsList(offsetsRight, doneButton.y + doneButton.height + 10, width - offsetsRight - 10, symbolsH))

    val categoryHeight = 15
    var freeX = offsetsRight
    var freeY = usedSymbolsList.y + 40 + 10
    tabsBackLeft = freeX
    tabsBackTop = freeY
    for (cat <- SymbolCategory.values()) {
      val w = mc.fontRenderer.getStringWidth(cat.name) + 10
      if (freeX + w > width - 10) {
        freeX = offsetsRight
        freeY += categoryHeight
      }
      addButton(new GuiTexturedButton(freeX, freeY, w, 15, cat.name, new Rectangle(2, 131, 64, 15))(() => currentSymbolCategory = cat))
      freeX += w
    }

    val symbolLibrary = addElement(new GuiTileList(this, offsetsRight, freeY + categoryHeight, width - 10 - offsetsRight, cancelButton.y - 10 - freeY + categoryHeight, 16, symbolLinesByLen)() {
      override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
        if (SelectedSymbol.get.isDefined)
          SelectedSymbol.clear()
        super.drawScreen(mouseX, mouseY, partialTicks)
      }
    })

    tabsBackRight = tabsBackLeft + symbolLibrary.w
    tabsBackBottom = symbolLibrary.y

    addFreeDrawable(() => {
      val page = SelectedSymbol.getPage
      if (!page.isEmpty) {
        GuiUtils.drawPage(Minecraft.getMinecraft.getTextureManager,
          0,
          page,
          16, 16,
          absMouseX, absMouseY
        )
      }
    })
  }

  override def onSelected(item: SymbolDrawable): Unit = {
    SelectedSymbol.set(item.name)
  }

  override def drawScreenPre(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreenPre(mouseX, mouseY, partialTicks)
    drawGradientRect(tabsBackLeft, tabsBackTop, tabsBackRight, tabsBackBottom, -1442840576, -1442840576)
  }

  override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
    if (mouseButton == 1)
      if (SelectedSymbol.get.isDefined)
        SelectedSymbol.clear()
    super.mouseClicked(mouseX, mouseY, mouseButton)
  }
}
