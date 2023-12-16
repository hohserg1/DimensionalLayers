package hohserg.dimensional.layers.gui.settings.solid

import hohserg.dimensional.layers.DimensionalLayersPreset
import hohserg.dimensional.layers.DimensionalLayersPreset.SolidLayerSpec
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.GuiNumericField
import hohserg.dimensional.layers.gui.GuiTileList.SelectHandler
import hohserg.dimensional.layers.gui.RelativeCoord.{alignLeft, alignTop}
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer
import hohserg.dimensional.layers.gui.settings.solid.GuiBlocksList.DrawableBlock
import net.minecraft.block.state.IBlockState
import net.minecraft.world.biome.Biome

import scala.collection.JavaConverters.mapAsScalaMapConverter

class GuiSettingsLayer(parent: GuiSetupDimensionalLayersPreset, layer: SolidLayerSpec, index: Int)
  extends GuiBaseSettingsLayer(parent, index) with SelectHandler[GuiBlocksList.DrawableBlock] {

  val fillerH = new ValueHolder[IBlockState](layer.filler)
  val biomeH = new ValueHolder[Biome](layer.biome)
  val heightH = new ValueHolder[Int](layer.height)

  override def buildLayerSpec(): DimensionalLayersPreset.LayerSpec = SolidLayerSpec(fillerH.get, biomeH.get, heightH.get)

  override def initGui(): Unit = {
    super.initGui()
    val rightPaneMinX = width - 190

    addElement(new GuiBlocksList(this, width - 210))
      .select(GuiBlocksList.DrawableBlock(fillerH.get.getBlock))

    val heightField = addElement(new GuiNumericField(
      x = rightPaneMinX + fontRenderer.getStringWidth("height:") + 3,
      y = doneButton.y + doneButton.height + 10,
      maxLen = 2,
      value = heightH,
      fromString = _.toInt
    ))

    addLabel("height:", alignLeft(rightPaneMinX), alignTop(heightField.y + 5), 0xffa0a0a0)
    addLabel("(cubes)", alignLeft(heightField.x + heightField.width + 4), alignTop(heightField.y + 5), 0xffa0a0a0)
    addLabel("properties: ", alignLeft(rightPaneMinX), alignTop(heightField.y + heightField.height + 20), 0xffa0a0a0)
    addFreeDrawable(() => {
      fillerH.get.getProperties.asScala.toSeq.zipWithIndex.foreach {
        case ((prop, value), index) =>
          drawString(fontRenderer, prop.getName + ": " + value, rightPaneMinX + 10, heightField.y + heightField.height + 30 + index * 10, 0xffa0a0a0)
      }
    })
  }

  override def onSelected(item: DrawableBlock): Unit = {
    if (fillerH.get.getBlock != item.block) {
      fillerH.set(item.block.getDefaultState)
    }
  }
}
