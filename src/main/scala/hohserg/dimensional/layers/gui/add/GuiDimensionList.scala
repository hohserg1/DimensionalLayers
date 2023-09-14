package hohserg.dimensional.layers.gui.add

import hohserg.dimensional.layers.gui.DimensionLogo
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.client.GuiScrollingList

import scala.collection.JavaConverters.asScalaSetConverter

object GuiDimensionList {
  final val slotWidth = DimensionLogo.width + 4
}

class GuiDimensionList(val parent: GuiAddLayer, availableWidth: Int, height: Int)
                      (val fitHorizontal: Int = availableWidth / GuiDimensionList.slotWidth)
  extends GuiScrollingList(
    Minecraft.getMinecraft,
    fitHorizontal * GuiDimensionList.slotWidth, height, 10, height - 10, 10, GuiDimensionList.slotWidth,
    Minecraft.getMinecraft.displayWidth, Minecraft.getMinecraft.displayHeight
  ) {

  val entries: Seq[GuiDimensionLine] = {
    val allDims = DimensionManager.getRegisteredDimensions.keySet().asScala.toIndexedSeq
    val lineCount = (allDims.size.toDouble / fitHorizontal).ceil.toInt
    for {
      i <- 0 until lineCount
      dimsAtLine = allDims.slice(i * fitHorizontal, (i + 1) * fitHorizontal)
    } yield
      new GuiDimensionLine(this, dimsAtLine)
  }

  override def getSize: Int = entries.size

  override def drawSlot(index: Int, right: Int, top: Int, height: Int, tess: Tessellator): Unit = {
    entries(index).drawEntry(left, top, mouseX, mouseY)
  }

  override def elementClicked(index: Int, doubleClick: Boolean): Unit = ()

  override def isSelected(index: Int): Boolean = false

  override def drawBackground(): Unit = ()

}
