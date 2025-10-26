package hohserg.dimensional.layers.gui.settings.additional.features

import hohserg.dimensional.layers.Main
import hohserg.dimensional.layers.gui.*
import hohserg.dimensional.layers.gui.DrawableArea.DumbContainer
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.GuiTileList.HolderSelectHandle
import hohserg.dimensional.layers.gui.settings.additional.features.GuiPotionList.DrawablePotion
import hohserg.dimensional.layers.gui.settings.solid.GuiBlocksList
import hohserg.dimensional.layers.gui.settings.solid.GuiBlocksList.DrawableBlock
import hohserg.dimensional.layers.preset.spec.{AdditionalFeature, BlockReplacing, PotionEffectGranting}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.Tessellator
import net.minecraft.init.{Blocks, MobEffects}
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation

import java.awt.Rectangle
import scala.collection.mutable.ListBuffer

val texture = new ResourceLocation(Main.modid, "textures/gui/additional_features.png")

enum FeatureType(textureX: Int, textureY: Int, textureW: Int, textureH: Int) extends Drawable with DrawableArea.Container {
  private val area = DrawableArea(
    RelativeCoord.horizontalCenterMin(textureW), RelativeCoord.alignTop(0),
    RelativeCoord.horizontalCenterMax(textureW), RelativeCoord.alignBottom(0),
    new Rectangle(textureX, textureY, textureW, textureH)
  )
  var minX: Int = 0
  var minY: Int = 0
  var maxX: Int = 0
  var maxY: Int = 0

  implicit def self: DrawableArea.Container = this

  override def draw(minX: Int, minY: Int, maxX: Int, maxY: Int): Unit = {
    this.minX = minX
    this.minY = minY
    this.maxX = maxX
    this.maxY = maxY
    drawTexturedRect(area.x, area.y, area.x2, area.y2, texture, area.uv._1, area.uv._2, area.uv._3, area.uv._4)
  }

  override def tooltip: String = ""

  case Potion extends FeatureType(0, 0, 21, 21)
  case BlockReplacement extends FeatureType(0, 22, 46, 21)
}

class GuiNewAdditionalFeature(parent: GuiBase, holder: ValueHolder[Seq[AdditionalFeature]])
  extends GuiBase(parent)
    with StateComposite {

  override val state = new ListBuffer[ValueHolder[?]]

  override def onStateChanged(): Unit = ()

  var selected = -1

  var confirmButton: GuiClickableButton = null
  var potionList: GuiPotionList = null
  var potionAmplifierField: GuiNumericField[Int] = null
  var blockFromList: GuiBlocksList = null
  var blockToList: GuiBlocksList = null

  val potionH = new ValueHolder[Potion](MobEffects.NIGHT_VISION)
  val blockFromH = new ValueHolder[IBlockState](Blocks.BEDROCK.getDefaultState)
  val blockToH = new ValueHolder[IBlockState](Blocks.STONE.getDefaultState)
  val potionAmplifierH = new ValueHolder[Int](0)

  val potionSelectHandler = new HolderSelectHandle[DrawablePotion, Potion](potionH, _.p)
  val blockFromSelectHandler = new HolderSelectHandle[DrawableBlock, IBlockState](blockFromH, _.block.getDefaultState)
  val blockToSelectHandler = new HolderSelectHandle[DrawableBlock, IBlockState](blockToH, _.block.getDefaultState)

  override def initGui(): Unit = {
    super.initGui()
    addButton(new GuiClickableButton(width - 100, height - 30, 90, 20, "Cancel")(back))
    confirmButton = addButton(new GuiClickableButton(width - 100, 10, 90, 20, "Add")(add))
    addElement(new GuiScrollingListElement(x = 10, y = 10, w = width / 4, h = height - 20, entryHeight = 21 + 4) {

      override def getSize: Int = FeatureType.values.length

      override def elementClicked(index: Int, bl: Boolean): Unit = selected = index

      override def isSelected(index: Int): Boolean = index == selected

      override def drawBackground(): Unit = ()

      override def drawSlot(index: Int, right: Int, top: Int, height: Int, tess: Tessellator): Unit = {
        if (FeatureType.values.indices contains index) {
          FeatureType.values(index).draw(left, top, right, top + height)
        }
      }
    })

    val listX = width / 4 + 20
    val listY = 40
    val listW = width - listX - 10
    val listH = height - listY * 2
    potionList = addElement(new GuiPotionList(potionSelectHandler, listX, listY, listW, listH - 30))
    potionList.select(DrawablePotion(potionH.get))

    val amplifierFieldX = width / 3 * 2
    val amplifierFieldY = potionList.y + potionList.h + 10
    potionAmplifierField = addElement(new GuiNumericField[Int](amplifierFieldX, amplifierFieldY, maxLen = 2, value = potionAmplifierH, _.toInt))
    val label = "amplifier: "
    val labelX = amplifierFieldX - fontRenderer.getStringWidth(label)
    addFreeDrawable(() => {
      if (selected == FeatureType.Potion.ordinal)
        drawString(fontRenderer,label, labelX, amplifierFieldY + 5, 0xffa0a0a0)
    })

    blockFromList = addElement(new GuiBlocksList(blockFromSelectHandler, listX, listY, listW / 2 - 11, listH))
    blockFromList.select(GuiBlocksList.DrawableBlock(blockFromH.get.getBlock))

    blockToList = addElement(new GuiBlocksList(blockToSelectHandler, listX + listW / 2 + 10, listY, listW / 2 - 10, listH))
    blockToList.select(GuiBlocksList.DrawableBlock(blockToH.get.getBlock))

    val arrowContainer = DumbContainer(blockFromList.x + blockFromList.w, 0, blockToList.x, height)

    addFreeDrawable(() => {
      if (selected == FeatureType.BlockReplacement.ordinal)
        drawWithTexture(texture, arrowArea.draw(_)(using arrowContainer))
    })
  }

  def add(): Unit = {
    if (FeatureType.values.indices contains selected) {
      holder.set(holder.get ++ Seq(
        FeatureType.values(selected) match {
          case FeatureType.Potion =>
            PotionEffectGranting(potionH.get, 0, true)
          case FeatureType.BlockReplacement =>
            BlockReplacing(blockFromH.get, blockToH.get)
        }
      ))
      back()
    }
  }

  override def drawScreenPre(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreenPre(mouseX, mouseY, partialTicks)
    confirmButton.enabled = selected != -1
    potionList.enabled = selected == FeatureType.Potion.ordinal
    potionAmplifierField.enabled = selected == FeatureType.Potion.ordinal
    blockFromList.enabled = selected == FeatureType.BlockReplacement.ordinal
    blockToList.enabled = selected == FeatureType.BlockReplacement.ordinal
  }
}
