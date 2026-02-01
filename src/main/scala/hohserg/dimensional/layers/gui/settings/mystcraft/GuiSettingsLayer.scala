package hohserg.dimensional.layers.gui.settings.mystcraft

import com.google.common.cache.LoadingCache
import com.xcompwiz.mystcraft.client.gui.GuiUtils
import com.xcompwiz.mystcraft.symbol.SymbolManager
import com.xcompwiz.mystcraft.symbol.modifiers.{SymbolBiome, SymbolBlock, SymbolColor}
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.RelativeCoord.{alignLeft, alignTop}
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer
import hohserg.dimensional.layers.gui.settings.additional.features.addAdditionalFeaturesWidgets
import hohserg.dimensional.layers.gui.settings.base.offsets.addOffsetsWidgets
import hohserg.dimensional.layers.gui.{GuiElement, GuiNumericField, GuiTextFieldElement, GuiTexturedButton, GuiTileList, Handler, IconUtils, StateComposite}
import hohserg.dimensional.layers.preset.spec.{AdditionalFeature, CubeOffsets, LayerSpec, MystcraftLayerSpec}
import hohserg.dimensional.layers.{clamp, toLongSeed}
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import java.awt.Rectangle
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

val nameToCat = SymbolCategory.values().flatMap(cat => cat.symbols.map(_.toLowerCase -> cat)).toMap

val symbolsByCat: Map[SymbolCategory, LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[DrawableSymbol]]]] =
  SymbolManager.getAgeSymbols.asScala.toSeq.groupBy({
    case color: SymbolColor => SymbolCategory.color
    case block: SymbolBlock => SymbolCategory.block
    case biome: SymbolBiome => SymbolCategory.biome
    case symbol => nameToCat.getOrElse(symbol.getRegistryName.getPath, SymbolCategory.other)
  }).map { case (cat, symbols) =>
    cat -> GuiTileList.createLinesCache(symbols.map(_.getRegistryName.toString).map(DrawableSymbol(_)), 16)
  }

@SideOnly(Side.CLIENT)
class GuiSettingsLayer(parent: GuiSetupDimensionalLayersPreset, layer: MystcraftLayerSpec, index: Int)
  extends GuiBaseSettingsLayer(parent, index)
    with GuiTileList.SelectHandler[DrawableSymbol] {

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

  private var currentSearchLibrary: GuiTileList[DrawableSymbol] = _

  override def initGui(): Unit = {
    super.initGui()

    val seedOverrideField = addElement(new GuiTextFieldElement(10, height / 2 - 20 - 20, 78, 20, seedOverrideH, identity))
    addCenteredLabel("seed override:", alignLeft(seedOverrideField.x + seedOverrideField.width / 2), alignTop(seedOverrideField.y - 13), 0xffa0a0a0)

    val offset = 70 + 10
    val offsetsLeft = IconUtils.width + 10 * 3 + offset
    val offsetsRight = offsetsLeft + 10 + offset
    addOffsetsWidgets(topOffset, bottomOffset, left = offsetsLeft, right = offsetsRight - offset)

    addAdditionalFeaturesWidgets(additionalFeaturesH, top = math.max(IconUtils.width + 10 * 6 + 9, height - 100), bottom = height - 10)

    //addInstabilityField()

    val usedSymbolsList = addElement(new UsedSymbolsList(offsetsRight, doneButton.y + doneButton.height + 10, width - offsetsRight - 10, symbolsH))

    val categoryHeight = 15
    tabsBackLeft = offsetsRight
    tabsBackTop = usedSymbolsList.y + 40 + 10
    val (catButtonFactories, (_, lastFreeY)) =
      SymbolCategory.values().foldLeft(Map[SymbolCategory, Handler => GuiTexturedButton]() -> (tabsBackLeft, tabsBackTop)) {
        case ((catToButtonFactory, (freeX, freeY)), cat) =>
          val w = mc.fontRenderer.getStringWidth(cat.name) + 10
          val (x, y) =
            if (freeX + w > width - 10)
              (offsetsRight, freeY + categoryHeight)
            else
              (freeX, freeY)

          (catToButtonFactory + (cat -> (new GuiTexturedButton(x, y, w, 15, cat.name, new Rectangle(2, 131, 64, 15))(_: Handler))), (x + w, y))
      }

    def createLibraryOf(symbols: LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[DrawableSymbol]]]): GuiTileList[DrawableSymbol] =
      new GuiTileList(this, offsetsRight, lastFreeY + categoryHeight * 2 + 2, width - 10 - offsetsRight, cancelButton.y - 10 - (lastFreeY + categoryHeight * 2 + 2), 16, symbols)()

    val libraries = symbolsByCat.map { case (cat, symbols: LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[DrawableSymbol]]]) =>
      cat -> addElement(createLibraryOf(symbols))
    }

    libraries.values.foreach(_.enabled = false)
    libraries.get(currentSymbolCategory).foreach(_.enabled = true)

    currentSearchLibrary = createLibraryOf(GuiTileList.createLinesCache(SymbolManager.getAgeSymbols.asScala.toSeq.map(_.getRegistryName.toString).map(DrawableSymbol(_)), 16))

    val searchField = addElement(new GuiTextFieldElement(offsetsRight + 2, lastFreeY + categoryHeight, width - 10 - offsetsRight - 4, categoryHeight, new ValueHolder[String]("")(using new StateComposite {
      override val state = new ListBuffer[ValueHolder[?]]

      override def onStateChanged(): Unit = {
        libraries.get(currentSymbolCategory).foreach(_.enabled = false)
        val request = state.head.asInstanceOf[ValueHolder[String]].get.toLowerCase
        val symbolsMixed = SymbolManager.getAgeSymbols.asScala.toSeq.filter(_.getLocalizedName.toLowerCase.contains(request))
        val (prefered, other) = symbolsMixed.partition(_.getLocalizedName.toLowerCase.startsWith(request))
        val symbols = GuiTileList.createLinesCache(
          (prefered ++ other).map(_.getRegistryName.toString).map(DrawableSymbol(_))
          , 16)
        currentSearchLibrary = createLibraryOf(symbols)
      }
    }), identity))

    catButtonFactories.foreach { case (cat, btn) =>
      libraries.get(cat).foreach(lib => {
        addButton(btn(() => {
          libraries.get(currentSymbolCategory).foreach(_.enabled = false)
          searchField.setText("")
          currentSearchLibrary.enabled = false
          lib.enabled = true
          currentSymbolCategory = cat
        }))
      })
    }

    addElement(new GuiElement {
      override def draw: Option[(Int, Int, Float) => Unit] = Some((mx, my, partialTicks) => currentSearchLibrary.draw.foreach(_(mx, my, partialTicks)))

      override def mouseInput: Option[(Int, Int) => Unit] = Some((mx, my) => currentSearchLibrary.mouseInput.foreach(_(mx, my)))

      override def mouseClick: Option[(Int, Int, Int) => Unit] = Some((mx, my, button) => currentSearchLibrary.mouseClick.foreach(_(mx, my, button)))

      override def mouseClickMove: Option[(Int, Int, Int) => Unit] = Some((mx, my, button) => currentSearchLibrary.mouseClickMove.foreach(_(mx, my, button)))

      override def mouseRelease: Option[(Int, Int, Int) => Unit] = Some((mx, my, button) => currentSearchLibrary.mouseRelease.foreach(_(mx, my, button)))

      override def keyTyped: Option[(Char, Int) => Unit] = Some((char, code) => currentSearchLibrary.keyTyped.foreach(_(char, code)))
    })

    tabsBackRight = tabsBackLeft + libraries.headOption.map(_._2.w).getOrElse(10)
    tabsBackBottom = libraries.headOption.map(_._2.y).getOrElse(tabsBackTop + 10)

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

  private def addInstabilityField(): Unit = {
    val instabilityField = addElement(new GuiNumericField(
      x = 10 + fontRenderer.getStringWidth("instability:") + 3,
      y = 10,
      maxLen = 3,
      value = instabilityH,
      fromString = _.toShort
    ))
    addLabel("instability:", alignLeft(10), alignTop(instabilityField.y + 5), 0xffa0a0a0)
  }

  override def onSelected(item: DrawableSymbol): Unit = {
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
