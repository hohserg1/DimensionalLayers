package hohserg.dimensional.layers.gui.preset.list

import hohserg.dimensional.layers.Main
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.{DrawableArea, GuiBase, IconUtils, RelativeCoord}
import hohserg.dimensional.layers.preset.spec.LayerSpec
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11

import java.awt.Rectangle

val texture = new ResourceLocation(Main.modid, "textures/gui/layer_entry.png")

val moveUp = DrawableArea(
  RelativeCoord.alignLeft(IconUtils.width + 11), RelativeCoord.alignTop(4),
  RelativeCoord.alignLeft(IconUtils.width + 11 + 26), RelativeCoord.alignTop(4 + 16),
  new Rectangle(2, 2, 26, 16)
)
val moveDown = DrawableArea(
  RelativeCoord.alignLeft(IconUtils.width + 11), RelativeCoord.alignBottom(-4 - 16),
  RelativeCoord.alignLeft(IconUtils.width + 11 + 26), RelativeCoord.alignBottom(-4),
  new Rectangle(2, 20, 26, 16)
)
val remove = DrawableArea(
  RelativeCoord.alignRight(-4 - 13), RelativeCoord.verticalCenterMin(12),
  RelativeCoord.alignRight(-4), RelativeCoord.verticalCenterMax(12),
  new Rectangle(2, 38, 13, 12)
)

val background = DrawableArea(
  RelativeCoord.alignLeft(0), RelativeCoord.alignTop(0),
  RelativeCoord.alignRight(0), RelativeCoord.alignBottom(0),
  new Rectangle(0, 0, 1, 1)
)

val rulers = DrawableArea(
  RelativeCoord.alignLeft(IconUtils.width + 2), RelativeCoord.alignTop(-3),
  RelativeCoord.alignLeft(IconUtils.width + 2 + 7), RelativeCoord.alignBottom(2),
  new Rectangle(150, 0, 7, 73),
  sameHoveringUV = true
)

val settings = DrawableArea(
  RelativeCoord.alignRight(-20 - 40), RelativeCoord.verticalCenterMin(20),
  RelativeCoord.alignRight(-40), RelativeCoord.verticalCenterMax(20),
  new Rectangle(2, 52, 20, 20)
)

@SideOnly(Side.CLIENT)
trait GuiLayerEntry extends DrawableArea.Container {

  implicit def self: DrawableArea.Container = this

  def parent: GuiLayersList

  def layer: LayerSpec

  def guiSettings(index: Int, parent: GuiSetupDimensionalLayersPreset): GuiBase

  protected val mc = Minecraft.getMinecraft

  var minX: Int = 0
  var minY: Int = 0
  var maxX: Int = 0
  var maxY: Int = 0
  var absMouseX: Int = 0
  var absMouseY: Int = 0

  def drawEntry(index: Int, minX: Int, minY: Int, maxX: Int, maxY: Int, mouseX: Int, mouseY: Int): Unit = {
    this.minX = minX
    this.minY = minY
    this.maxX = maxX
    this.maxY = maxY
    this.absMouseX = mouseX
    this.absMouseY = mouseY

    mc.getTextureManager.bindTexture(texture)

    val tess = Tessellator.getInstance()
    val buffer = tess.getBuffer
    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

    rulers.draw(buffer)

    if (background.isHovering) {
      if (isNotFirst(index))
        moveUp.draw(buffer)

      if (isNotLast(index))
        moveDown.draw(buffer)

      remove.draw(buffer)
      settings.draw(buffer)
    }

    tess.draw()

    val endCubeY = parent.startCubeY.get + parent.entries.dropWhile(_ != this).map(_.layer.height).sum

    mc.fontRenderer.drawStringWithShadow("" + endCubeY, minX + IconUtils.width + 11, minY - 6, 0xffffff)
  }

  def clicked(index: Int, mouseX: Int, mouseY: Int): Unit = {
    checkMoveUpClicked(index, mouseX, mouseY)
    checkMoveDownClicked(index, mouseX, mouseY)
    checkRemoveClicked(index, mouseX, mouseY)
    checkSettingsClicked(index, mouseX, mouseY)
    parent.parent.exportButton.displayString = "Export preset"
  }

  private def checkMoveUpClicked(index: Int, mouseX: Int, mouseY: Int): Unit = {
    if (isNotFirst(index)) {
      if (moveUp.isHovering) {
        val prev = parent.entries(index - 1)
        parent.entries(index - 1) = this
        parent.entries(index) = prev
        parent.scrollUpOnce();
      }
    }
  }

  private def checkMoveDownClicked(index: Int, mouseX: Int, mouseY: Int): Unit = {
    if (isNotLast(index)) {
      if (moveDown.isHovering) {
        val next = parent.entries(index + 1)
        parent.entries(index + 1) = this
        parent.entries(index) = next
        parent.scrollDownOnce();
      }
    }
  }

  private def checkRemoveClicked(index: Int, mouseX: Int, mouseY: Int): Unit = {
    if (remove.isHovering) {
      parent.entries.remove(index)
    }
  }

  private def checkSettingsClicked(index: Int, mouseX: Int, mouseY: Int): Unit = {
    if (settings.isHovering) {
      mc.displayGuiScreen(guiSettings(index, parent.parent))
    }
  }

  private def isNotLast(index: Int) = {
    index != parent.entries.size - 1
  }

  private def isNotFirst(index: Int) = {
    index != 0
  }
}
