package hohserg.dimension.layers.gui

import hohserg.dimension.layers.DimensionLayersPreset.LayerSpec
import hohserg.dimension.layers.Memoized
import hohserg.dimension.layers.gui.GuiLayersList._
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.world.DimensionType
import net.minecraftforge.fml.client.GuiScrollingList
import org.lwjgl.input.Mouse

import java.awt.Rectangle
import java.lang.reflect.Field

class GuiLayersList(parent: GuiSetupDimensionLayersPreset)
  extends GuiScrollingList(Minecraft.getMinecraft, 307, parent.height, 10, parent.height - 10, 10, height + betweenSlotsOffset * 2, parent.width, parent.height) {

  override def getSize: Int = parent.currentPreset.layers.size

  override def elementClicked(index: Int, doubleClick: Boolean): Unit = {
    println("elementClicked", index)
  }

  override def isSelected(index: Int): Boolean = false

  override def drawBackground(): Unit = {}

  override def drawSlot(index: Int, right: Int, slotTop: Int, h: Int, tess: Tessellator): Unit = {
    val layerSpec = parent.currentPreset.layers(index)

    val top = slotTop + betweenSlotsOffset
    val bottom = top + height

    GlStateManager.color(1, 1, 1, 1)

    drawSlotBg(layerSpec, right, top, bottom, tess)

    drawSlotButtons(layerSpec, index, right, top, bottom, tess)

    mc.fontRenderer.drawStringWithShadow(layerSpec.dimensionType.getName, left + 2, top + height / 2 - 5, 0xffffff)
  }

  var movingSlider: Option[Int] = None

  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    if (movingSlider.isDefined) {
      val prev = getScrollDistance
      super.drawScreen(mouseX, mouseY, partialTicks)
      setScrollDistance(prev)
      super.drawScreen(mouseX, mouseY, partialTicks)
    } else {
      //println(isUnderSlider)
      super.drawScreen(mouseX, mouseY, partialTicks)
    }
  }

  val sliders = (0 until getSize).map(_ => 0).toBuffer

  private def drawSlotButtons(layerSpec: LayerSpec, index: Int, right: Int, top: Int, bottom: Int, tess: Tessellator): Unit = {
    mc.getTextureManager.bindTexture(new ResourceLocation("textures/gui/resource_packs.png"))

    val (mouseX, mouseY) = MouseUtils.getMousePos(parent)

    val offset = 8

    val upButtonRect = new Rectangle(left + offset, top + offset, 11, 7)
    val downButtonRect = new Rectangle(left + offset, bottom - 7 - offset, 11, 7)

    val isUnderUp = upButtonRect.contains(mouseX, mouseY)
    val isUnderDown = downButtonRect.contains(mouseX, mouseY)

    parent.drawTexturedModalRect(upButtonRect.x, upButtonRect.y, 114, if (isUnderUp) 37 else 5, upButtonRect.width, upButtonRect.height)
    parent.drawTexturedModalRect(downButtonRect.x, downButtonRect.y, 82, if (isUnderDown) 52 else 20, downButtonRect.width, downButtonRect.height)

    val sliderPos = sliders(index)
    val heightSlider = new Rectangle(right - 8, top - 6 + sliderPos * 64 / 16, 7, 11)
    val isUnderSlider = heightSlider.contains(mouseX, mouseY)

    if (Mouse.isButtonDown(0)) {
      if (isUnderSlider && movingSlider.isEmpty)
        movingSlider = Some(index)
    } else
      movingSlider = None

    GlStateManager.scale(0.5, 0.5, 1)
    parent.drawTexturedModalRect(heightSlider.x * 2, heightSlider.y * 2, 34, if (isUnderSlider) 37 else 5, heightSlider.width * 2, heightSlider.height * 2)
    GlStateManager.scale(2, 2, 1)

    if (movingSlider.contains(index))
      sliders(index) = MathHelper.clamp(mouseY - top, 0, 64) / 4

  }


  val missingBg = new ResourceLocation("textures/gui/dimension_layers_background/missing.png")

  val getBackgroundForDimensionType: DimensionType => ResourceLocation =
    Memoized((dimensionType: DimensionType) => {
      val Array(modid, dimName) = ResourceLocation.splitObjectName(dimensionType.getName)
      val bgLocation = new ResourceLocation(modid, "textures/gui/dimension_layers_background/" + dimName + ".png")

      try {
        if (mc.getResourceManager.getResource(bgLocation) != null)
          bgLocation
        else
          missingBg
      } catch {
        case exception: Exception =>
          missingBg
      }
    })

  private def drawSlotBg(layerSpec: LayerSpec, right: Int, top: Int, bottom: Int, tess: Tessellator): Unit = {
    mc.getTextureManager.bindTexture(getBackgroundForDimensionType(layerSpec.dimensionType))

    val buffer = tess.getBuffer
    buffer.begin(7, DefaultVertexFormats.POSITION_TEX)
    val z = -100
    buffer.pos(left, bottom, z).tex(0, 1).endVertex()
    buffer.pos(right, bottom, z).tex(1, 1).endVertex()
    buffer.pos(right, top, z).tex(1, 0).endVertex()
    buffer.pos(left, top, z).tex(0, 0).endVertex()
    tess.draw()
  }

  override def handleMouseInput(mouseX: Int, mouseY: Int): Unit = {
    val isHovering = mouseX >= this.left && mouseX <= this.left + this.listWidth && mouseY >= this.top && mouseY <= this.bottom
    if (isHovering) {

      val scroll = Mouse.getEventDWheel
      if (scroll != 0)
        setScrollDistance(getScrollDistance + ((-1 * scroll / 120f / 3) * this.slotHeight / 2))
    }
  }

  val scrollDistance: Field = {
    val f = classOf[GuiScrollingList].getDeclaredField("scrollDistance")
    f.setAccessible(true)
    f
  }

  private def getScrollDistance: Float =
    scrollDistance.getFloat(this)

  private def setScrollDistance(v: Float): Unit =
    scrollDistance.setFloat(this, v)
}

object GuiLayersList {
  def mc = Minecraft.getMinecraft

  val betweenSlotsOffset = 4
  val height = 64

}
