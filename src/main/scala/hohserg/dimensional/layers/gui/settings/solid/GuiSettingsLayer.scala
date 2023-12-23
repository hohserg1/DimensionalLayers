package hohserg.dimensional.layers.gui.settings.solid

import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.GuiTileList.SelectHandler
import hohserg.dimensional.layers.gui.RelativeCoord.{alignBottom, alignLeft, alignTop}
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer
import hohserg.dimensional.layers.gui.settings.solid.GuiBlocksList.DrawableBlock
import hohserg.dimensional.layers.gui.{GuiNumericField, GuiScrollingListElement}
import hohserg.dimensional.layers.preset.{LayerSpec, SolidLayerSpec}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.Tessellator
import net.minecraft.world.biome.Biome
import net.minecraftforge.fml.common.registry.ForgeRegistries

import scala.collection.JavaConverters.{collectionAsScalaIterableConverter, mapAsScalaMapConverter}

class GuiSettingsLayer(parent: GuiSetupDimensionalLayersPreset, layer: SolidLayerSpec, index: Int)
  extends GuiBaseSettingsLayer(parent, index) with SelectHandler[GuiBlocksList.DrawableBlock] {

  val fillerH = new ValueHolder[IBlockState](layer.filler)
  val biomeH = new ValueHolder[Biome](layer.biome)
  val heightH = new ValueHolder[Int](layer.height)

  override def buildLayerSpec(): LayerSpec = SolidLayerSpec(fillerH.get, heightH.get, biomeH.get)

  override def initGui(): Unit = {
    super.initGui()
    val rightPaneMinX = width - 190

    val heightField = addElement(new GuiNumericField(
      x = rightPaneMinX + fontRenderer.getStringWidth("height:") + 3,
      y = doneButton.y + doneButton.height + 10,
      maxLen = 2,
      value = heightH,
      fromString = _.toInt
    ))

    addLabel("height:", alignLeft(rightPaneMinX), alignTop(heightField.y + 5), 0xffa0a0a0)
    addLabel("(cubes)", alignLeft(heightField.x + heightField.width + 4), alignTop(heightField.y + 5), 0xffa0a0a0)
    addLabel("properties: ", alignLeft(rightPaneMinX), alignBottom(-20 - 10 * 4), 0xffa0a0a0)
    addFreeDrawable(() => {
      fillerH.get.getProperties.asScala.toSeq.zipWithIndex.foreach {
        case ((prop, value), index) =>
          drawString(fontRenderer, prop.getName + ": " + value, rightPaneMinX + 10, height - 10 - 10 * 4 + index * 10, 0xffa0a0a0)
      }
    })

    val yy = heightField.y + heightField.width + 10
    addElement(new GuiScrollingListElement(rightPaneMinX, yy, width - rightPaneMinX - 15, height - yy - 20 - 10 * 4 - 10, fontRenderer.FONT_HEIGHT + 2) {
      val possibles = ForgeRegistries.BIOMES.getValuesCollection.asScala.toIndexedSeq

      var selected: Int = possibles.indexOf(biomeH.get)
      scrollToElement(selected)

      override def getSize: Int = possibles.size

      override def elementClicked(index: Int, doubleClick: Boolean): Unit = {
        selected = index
        if (possibles.indices contains index)
          biomeH.set(possibles(index))
      }

      override def isSelected(index: Int): Boolean =
        selected == index

      override def drawBackground(): Unit = ()

      override def drawSlot(index: Int, right: Int, top: Int, height: Int, tess: Tessellator): Unit = {
        if (possibles.indices contains index) {
          val biome = possibles(index)
          fontRenderer.drawString(biome.biomeName, left + 2, top, 0xffff00ff)
        }
      }
    })

    addElement(new GuiBlocksList(this, width - 210))
      .select(GuiBlocksList.DrawableBlock(fillerH.get.getBlock))
  }

  override def onSelected(item: DrawableBlock): Unit = {
    if (fillerH.get.getBlock != item.block) {
      fillerH.set(item.block.getDefaultState)
    }
  }
}
