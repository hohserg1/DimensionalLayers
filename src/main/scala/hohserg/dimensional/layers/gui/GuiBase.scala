package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.Main
import hohserg.dimensional.layers.gui.RelativeCoord.{alignLeft, alignTop}
import net.minecraft.client.gui.{FontRenderer, GuiScreen}
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.mutable.ListBuffer

@SideOnly(Side.CLIENT)
class GuiBase(val parent: GuiScreen) extends GuiScreen {
  def fr: FontRenderer = fontRenderer

  implicit def self: this.type = this

  protected def back(): Unit = {
    mc.displayGuiScreen(parent)
  }

  protected def show(nextGuiByParent: this.type => GuiScreen): Handler =
    () => mc.displayGuiScreen(nextGuiByParent(this))

  protected def showWarning(msg: String, desc: String): Handler =
    () => Main.sided.printSimpleError(msg, desc)

  private var elementId = 0

  private val drawScreenPostListeners = new ListBuffer[(Int, Int, Float) => Unit]()
  private val handleMouseInputListeners = new ListBuffer[(Int, Int) => Unit]()
  private val mouseClickedListeners = new ListBuffer[(Int, Int, Int) => Unit]()
  private val mouseClickMoveListeners = new ListBuffer[(Int, Int, Int) => Unit]()
  private val mouseReleasedListeners = new ListBuffer[(Int, Int, Int) => Unit]()
  private val keyTypedListeners = new ListBuffer[(Char, Int) => Unit]()

  def nextElementId(): Int = {
    elementId += 1
    elementId
  }

  override def initGui(): Unit = {
    super.initGui()
    elementId = 0
    drawScreenPostListeners.clear()
    handleMouseInputListeners.clear()
    mouseClickedListeners.clear()
    mouseClickMoveListeners.clear()
    mouseReleasedListeners.clear()
    keyTypedListeners.clear()
  }

  def addElement[E <: GuiElement](e: E): E = {
    drawScreenPostListeners ++= e.draw
    handleMouseInputListeners ++= e.mouseInput
    mouseClickedListeners ++= e.mouseClick
    mouseClickMoveListeners ++= e.mouseClickMove
    mouseReleasedListeners ++= e.mouseRelease
    keyTypedListeners ++= e.keyTyped

    e
  }

  def addLabel(text: String, x: Int, y: Int, color: Int): Unit = {
    addLabel(text, alignLeft(x), alignTop(y), color)
  }

  def addLabel(text: String, x: RelativeCoord, y: RelativeCoord, color: Int): Unit = {
    drawScreenPostListeners += {
      (_, _, _) => drawString(fontRenderer, text, x.absoluteCoord(0, 0, width, height), y.absoluteCoord(0, 0, width, height), color)
    }
  }

  def addLink(text: String, link: String, x: RelativeCoord, y: RelativeCoord): Unit = {
    addLabel(TextFormatting.UNDERLINE.toString + text, x, y, 0xFF5555FF)
    mouseClickedListeners += {
      (mouseX, mouseY, _) =>
        val currX = x.absoluteCoord(0, 0, width, height)
        val currY = y.absoluteCoord(0, 0, width, height)
        if (currX <= mouseX && mouseX <= currX + fr.getStringWidth(text) &&
          currY <= mouseY && mouseY <= currY + fr.FONT_HEIGHT
        ) {
          val guiConfirmOpenLink = new GuiAcceptOpenLink(this, link)
          guiConfirmOpenLink.disableSecurityWarning()
          mc.displayGuiScreen(guiConfirmOpenLink)
        }
    }
  }

  override def confirmClicked(result: Boolean, id: Int): Unit = {
    super.confirmClicked(result, 31102009)
  }

  def addCenteredLabel(text: String, x: RelativeCoord, y: RelativeCoord, color: Int): Unit = {
    drawScreenPostListeners += {
      (_, _, _) => drawCenteredString(fontRenderer, text, x.absoluteCoord(0, 0, width, height), y.absoluteCoord(0, 0, width, height), color)
    }
  }

  def addFreeDrawable(draw: () => Unit): Unit = {
    drawScreenPostListeners += { (_, _, _) => draw() }
  }

  def drawScreenPre(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    drawDefaultBackground()
  }

  def drawScreenPost(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    drawScreenPostListeners.foreach(_(mouseX, mouseY, partialTicks))
  }

  var absMouseX: Int = 0
  var absMouseY: Int = 0

  override final def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    absMouseX = mouseX
    absMouseY = mouseY
    drawScreenPre(mouseX, mouseY, partialTicks)
    super.drawScreen(mouseX, mouseY, partialTicks)
    GlStateManager.color(1, 1, 1, 1)
    drawScreenPost(mouseX, mouseY, partialTicks)
  }

  override def handleMouseInput(): Unit = {
    super.handleMouseInput()
    handleMouseInputListeners.foreach(_(absMouseX, absMouseY))
  }

  override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
    super.mouseClicked(mouseX, mouseY, mouseButton)
    mouseClickedListeners.foreach(_(mouseX, mouseY, mouseButton))
  }

  override def mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long): Unit = {
    super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
    mouseClickMoveListeners.foreach(_(mouseX, mouseY, clickedMouseButton))
  }

  override def mouseReleased(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
    super.mouseReleased(mouseX, mouseY, mouseButton)
    mouseReleasedListeners.foreach(_(mouseX, mouseY, mouseButton))
  }

  override def keyTyped(typedChar: Char, keyCode: Int): Unit = {
    if (keyCode == 1) {
      back()
      if (this.mc.currentScreen == null)
        this.mc.setIngameFocus()
    } else {
      keyTypedListeners.foreach(_(typedChar, keyCode))
    }
  }
}
