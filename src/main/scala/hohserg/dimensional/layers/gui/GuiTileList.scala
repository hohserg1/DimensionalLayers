package hohserg.dimensional.layers.gui

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import hohserg.dimensional.layers.gui.GuiTileList.{GuiTileLine, SelectHandler, slotWidth}
import hohserg.dimensional.layers.gui.mixin.AccessorGuiScrollingList
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraftforge.fml.client.GuiScrollingList
import org.lwjgl.opengl.GL11


object GuiTileList {

  trait SelectHandler[A <: Drawable] {
    def onSelected(item: A): Unit
  }

  def createLinesCache[A <: Drawable](allItems: Seq[A], itemWidth: Int): LoadingCache[Integer, Seq[GuiTileLine[A]]] =
    CacheBuilder.newBuilder()
      .maximumSize(4)
      .build(new CacheLoader[Integer, Seq[GuiTileLine[A]]] {
        override def load(len: Integer): Seq[GuiTileLine[A]] = {
          allItems.sliding(len, len).map(new GuiTileLine(_, itemWidth)).toIndexedSeq
        }
      })

  class GuiTileLine[A <: Drawable](val line: Seq[A], itemWidth: Int) {

    protected var minX: Int = 0
    protected var minY: Int = 0
    protected var maxX: Int = 0
    protected var maxY: Int = 0
    protected var mouseX: Int = 0
    protected var mouseY: Int = 0

    def drawEntryAndGetTooltip(verticalIndex: Int, minX: Int, minY: Int, maxX: Int, maxY: Int, mouseX: Int, mouseY: Int): Option[String] = {
      this.minX = minX
      this.minY = minY
      this.maxX = maxX
      this.maxY = maxY
      this.mouseX = mouseX
      this.mouseY = mouseY

      var r: Option[String] = None

      for ((item, horizontalIndex) <- line.zipWithIndex) {
        val x = minX + horizontalIndex * slotWidth(itemWidth) + border
        val y = minY + border
        if (isHovering(x, y, x + itemWidth, y + itemWidth)) {
          drawHighlight(x, y, 136, 146, 201)
          r = Some(item.tooltip)
        }
        item.draw(x, y, x + itemWidth, y + itemWidth)
      }
      r
    }

    def drawSelection(horizontalIndex: Int): Unit = {
      val x = minX + horizontalIndex * slotWidth(itemWidth) + border
      val y = minY + border
      drawHighlight(x, y)
    }

    def drawHighlight(xx: Int, yy: Int, red: Int = 255, green: Int = 255, blue: Int = 255): Unit = {
      GlStateManager.disableTexture2D()
      val z = -100
      val tess = Tessellator.getInstance()
      val buffer = tess.getBuffer
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)

      buffer.pos(xx - 1, yy, z).color(red, green, blue, 255).endVertex()
      buffer.pos(xx + itemWidth + 1, yy, z).color(red, green, blue, 255).endVertex()
      buffer.pos(xx + itemWidth + 1, yy - 1, z).color(red, green, blue, 255).endVertex()
      buffer.pos(xx - 1, yy - 1, z).color(red, green, blue, 255).endVertex()


      buffer.pos(xx - 1, yy + itemWidth + 1, z).color(red, green, blue, 255).endVertex()
      buffer.pos(xx, yy + itemWidth + 1, z).color(red, green, blue, 255).endVertex()
      buffer.pos(xx, yy - 1, z).color(red, green, blue, 255).endVertex()
      buffer.pos(xx - 1, yy - 1, z).color(red, green, blue, 255).endVertex()


      buffer.pos(xx + itemWidth, yy - 1, z).color(red, green, blue, 255).endVertex()
      buffer.pos(xx + itemWidth, yy + itemWidth + 1, z).color(red, green, blue, 255).endVertex()
      buffer.pos(xx + itemWidth + 1, yy + itemWidth + 1, z).color(red, green, blue, 255).endVertex()
      buffer.pos(xx + itemWidth + 1, yy - 1, z).color(red, green, blue, 255).endVertex()


      buffer.pos(xx - 1, yy + itemWidth + 1, z).color(red, green, blue, 255).endVertex()
      buffer.pos(xx + itemWidth + 1, yy + itemWidth + 1, z).color(red, green, blue, 255).endVertex()
      buffer.pos(xx + itemWidth + 1, yy + itemWidth, z).color(red, green, blue, 255).endVertex()
      buffer.pos(xx - 1, yy + itemWidth, z).color(red, green, blue, 255).endVertex()

      tess.draw()
      GlStateManager.enableTexture2D()

    }

    def clicked(): Option[(Int, A)] = {
      for (horizontalIndex <- line.indices) {
        val x = minX + horizontalIndex * slotWidth(itemWidth) + border
        val y = minY + border
        if (isHovering(x, y, x + itemWidth, y + itemWidth)) {
          return Some(horizontalIndex, line(horizontalIndex))
        }
      }
      None
    }

    private def isHovering(minX: Int, minY: Int, maxX: Int, maxY: Int) =
      minX <= mouseX && mouseX < maxX &&
        minY <= mouseY && mouseY < maxY
  }

  val border = 4

  def slotWidth(itemWidth: Int): Int = itemWidth + border * 2
}

abstract class GuiTileList[A <: Drawable](val parent: GuiScreen with SelectHandler[A], availableWidth: Int, height: Int, itemWidth: Int, linesCache: LoadingCache[Integer, Seq[GuiTileLine[A]]])
                                         (val fitHorizontal: Int = (availableWidth - 6) / slotWidth(itemWidth))
  extends GuiScrollingList(
    Minecraft.getMinecraft,
    fitHorizontal * slotWidth(itemWidth) + 6, height, 10, height - 10, 10, slotWidth(itemWidth),
    Minecraft.getMinecraft.displayWidth, Minecraft.getMinecraft.displayHeight
  ) {
  val lines = linesCache.get(fitHorizontal)

  var selection: Option[(Int, Int, A)] = None

  override def getSize: Int = lines.size

  private var maybeTooltip: Option[String] = None

  private val drawHoveringText: (String, Int, Int) => Unit = {
    val g = new GuiScreen {}
    g.setWorldAndResolution(Minecraft.getMinecraft, Minecraft.getMinecraft.displayWidth, Minecraft.getMinecraft.displayHeight)
    g.drawHoveringText
  }

  override def elementClicked(verticalIndex: Int, doubleClick: Boolean): Unit =
    if (lines.indices contains verticalIndex)
      lines(verticalIndex).clicked() match {
        case Some((horizontalIndex, element)) =>
          selection = Some(verticalIndex, horizontalIndex, element)
          parent.onSelected(element)
        case None =>
      }

  override def isSelected(index: Int): Boolean = false

  override def drawBackground(): Unit = ()

  override def drawSlot(index: Int, right: Int, top: Int, height: Int, tess: Tessellator): Unit = {
    if (lines.indices contains index) {
      val line = lines(index)
      val maybeNewTooltip = line.drawEntryAndGetTooltip(index, left, top, right, top + height, mouseX, mouseY)
      maybeTooltip = maybeTooltip.orElse(maybeNewTooltip)

      selection match {
        case Some((verticalIndex, horizontalIndex, _)) if index == verticalIndex =>
          line.drawSelection(horizontalIndex)

        case _ =>
      }
    }
  }

  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreen(mouseX, mouseY, partialTicks)
    maybeTooltip.foreach(drawHoveringText(_, mouseX, mouseY))
    maybeTooltip = None
  }

  def select(item: A): Unit = {
    for ((line, verticalIndex) <- lines.zipWithIndex) {
      for ((i, horizontalIndex) <- line.line.zipWithIndex) {
        if (i == item) {
          selection = Some((verticalIndex, horizontalIndex, item))
          this.asInstanceOf[AccessorGuiScrollingList].setScrollDistance(slotHeight * verticalIndex)
          return
        }
      }
    }
  }
}