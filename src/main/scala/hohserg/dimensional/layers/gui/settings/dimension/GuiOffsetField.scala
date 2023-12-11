package hohserg.dimensional.layers.gui.settings.dimension

import hohserg.dimensional.layers.gui.GuiNumericField.NumberHolder
import hohserg.dimensional.layers.gui.settings.dimension.GuiOffsetField._
import hohserg.dimensional.layers.gui.settings.dimension.GuiSettingsLayer.{gridCellSize, gridLeft, texture}
import hohserg.dimensional.layers.gui.{DrawableArea, GuiNumericField}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import org.lwjgl.opengl.GL11

import java.awt.Rectangle

object GuiOffsetField {
  val top = DrawableArea(
    new Rectangle(-80, -4, 138, 15),
    new Rectangle(53, 0, 138, 15),
    new Rectangle(53, 16, 138, 15)
  )

  val bottom = DrawableArea(
    new Rectangle(-96, -4, 154, 15),
    new Rectangle(37, 32, 154, 15),
    new Rectangle(37, 48, 154, 15)
  )
}

class GuiOffsetField(id: Int, gridTop: Int, value: NumberHolder[Int], originalValue: Int, topCompanion: GuiOffsetField)
                    (implicit fontRenderer: FontRenderer)
  extends GuiNumericField[Int](id, gridLeft + 19, 0, 2, value, originalValue, _.toInt, fontRenderer.FONT_HEIGHT)
    with DrawableArea.Container {
  implicit def self: DrawableArea.Container = this

  val isTop = topCompanion == null

  setEnableBackgroundDrawing(false)

  val label = if (isTop) "top offset" else "bottom offset"
  val area = if (isTop) top else bottom

  override def minX: Int = x

  override def minY: Int = y

  override def maxX: Int = x + width

  override def maxY: Int = y + height


  override def setText(textIn: String): Unit = {
    super.setText(textIn)
    updateY()
  }

  private def updateY(): Unit = {
    y =
      if (isTop)
        gridTop + value.get * gridCellSize - 3
      else
        gridTop + (16 - value.get) * gridCellSize - 3
  }

  override def drawTextBox(): Unit = {
    GlStateManager.color(1, 1, 1, 1)
    Minecraft.getMinecraft.getTextureManager.bindTexture(texture)
    val tess = Tessellator.getInstance()
    val buffer = tess.getBuffer
    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
    area.draw(buffer)
    tess.draw()

    super.drawTextBox()

    drawString(fontRenderer, label, x - fontRenderer.getStringWidth(label) - 24, y, 0xffa0a0a0)
    drawString(fontRenderer, "(cubes)", x + fontRenderer.getStringWidth("99") + 2, y, 0xffa0a0a0)
  }

  override def setFocused(isFocusedIn: Boolean): Unit = {
    super.setFocused(isFocusedIn)
    if (!isFocusedIn)
      setText(value.get.toString)
  }

  private var clickedY: Option[Int] = None

  override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean = {
    if (area.isHovering && (isTop || !topCompanion.area.isHovering)) {
      clickedY = Some(mouseY)
      setFocused(false)
      true
    } else
      false
  }

  override def mouseClickMove: Option[(Int, Int, Int) => Unit] = Some((_, mouseY, _) => {
    if (clickedY.isDefined) {
      val i =
        if (isTop)
          (mouseY + 7 + 3 - gridTop) / gridCellSize
        else
          -((mouseY + 7 + 3 - gridTop) / gridCellSize - 16)
      setText(i.toString)
    }
  })

  override def mouseRelease: Option[(Int, Int, Int) => Unit] = Some((mouseX, mouseY, mouseButton) => {
    val yx = clickedY.map(_ - mouseY).getOrElse(0)
    if (yx < gridCellSize / 2)
      super.mouseClicked(mouseX, mouseY, mouseButton)
    clickedY = None
  })
}
