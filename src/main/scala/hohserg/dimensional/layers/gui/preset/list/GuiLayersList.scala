package hohserg.dimensional.layers.gui.preset.list

import hohserg.dimensional.layers.clamp
import hohserg.dimensional.layers.data.LayerMap
import hohserg.dimensional.layers.gui.*
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.preset.*
import hohserg.dimensional.layers.preset.spec.LayerSpec
import net.minecraft.client.renderer.Tessellator
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Try

@SideOnly(Side.CLIENT)
class GuiLayersList(val parent: GuiSetupDimensionalLayersPreset, settings: String, scrollDistance: Float)
  extends GuiScrollingListElement(10, 10, parent.width - 200, parent.height - 20, IconUtils.width + 4) {

  private val fullPresetBase: DimensionalLayersPreset = DimensionalLayersPreset.fromJson(settings)
  private val fromPreset = fullPresetBase.realDimensionToLayers(parent.currentRealDimension)

  val startCubeY: ValueHolder[Int] = new ValueHolder[Int](fromPreset.startCubeY, clamp(_, LayerMap.minCubeY, LayerMap.maxCubeY))(using new StateComposite {
    override val state = new ListBuffer[ValueHolder[?]]

    override def onStateChanged(): Unit = ()
  })

  val startCubeYField = new GuiCubeYField(10 + IconUtils.width + 11, 10 + parent.height - 30, startCubeY)(using parent)

  val entries: mutable.Buffer[GuiLayerEntry] =
    fromPreset.layers
              .map(_.toGuiLayerEntry(this))
              .toBuffer

  setScrollDistanceWithLimits(scrollDistance)

  def add(layer: LayerSpec): Unit = {
    layer.toGuiLayerEntry(this) +=: entries
  }

  def scrollUpOnce(): Unit = {
    setScrollDistanceWithLimits(GuiScrollingListLens.scrollDistance.get(this) - this.slotHeight)
  }

  def scrollDownOnce(): Unit = {
    setScrollDistanceWithLimits(GuiScrollingListLens.scrollDistance.get(this) + this.slotHeight)
  }

  def toSettings: String = Try(actualPreset.toSettings).getOrElse("")


  def actualPreset: DimensionalLayersPreset =
    fullPresetBase.copy(realDimensionToLayers = fullPresetBase.realDimensionToLayers.updated(
      parent.currentRealDimension, 
      SingleDimensionPreset(entries.map(_.layer).toList, startCubeY.get)
    ))


  override def getSize: Int = entries.size

  def entryHeight = slotHeight

  override def drawSlot(index: Int, right: Int, top: Int, height: Int, tess: Tessellator): Unit = {
    if (entries.indices contains index)
      entries(index).drawEntry(index, left, top, right, top + height, mouseX, mouseY)
  }

  override def elementClicked(index: Int, doubleClick: Boolean): Unit = {
    if (entries.indices contains index)
      entries(index).clicked(index, mouseX, mouseY)
  }

  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreen(mouseX, mouseY, partialTicks)

    val baseY = top + 4 - GuiScrollingListLens.scrollDistance.get(this).toInt
    startCubeYField.y = baseY + entries.size * this.slotHeight - 11
    startCubeYField.drawTextBox()
  }
  
  override def drawHoveringHighlight(): Unit = ()

  override def mouseClick: Option[(Int, Int, Int) => Unit] = startCubeYField.mouseClick

  override def keyTyped: Option[(Char, Int) => Unit] = startCubeYField.keyTyped

  override def isSelected(index: Int): Boolean = false

  override def drawBackground(): Unit = ()
}
