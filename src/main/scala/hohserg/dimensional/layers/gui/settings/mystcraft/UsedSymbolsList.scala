package hohserg.dimensional.layers.gui.settings.mystcraft

import com.xcompwiz.mystcraft.client.gui.GuiUtils
import com.xcompwiz.mystcraft.client.gui.element.{FixGuiElementScrollablePages, GuiElementScrollablePages}
import com.xcompwiz.mystcraft.page.Page
import hohserg.dimensional.layers.data.layer.mystcraft.symbolsToPages
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.GuiElement
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import java.util
import scala.jdk.CollectionConverters.*

@SideOnly(Side.CLIENT)
class UsedSymbolsList(x: Int, val y: Int, w: Int, symbolsH: ValueHolder[Seq[String]]) extends GuiElement
  with GuiElementScrollablePages.IGuiScrollableClickHandler
  with GuiElementScrollablePages.IGuiPageListProvider {

  val delegate = new GuiElementScrollablePages(
    this,
    this,
    Minecraft.getMinecraft,
    x, y, w, 40
  )
  FixGuiElementScrollablePages.dimensionalLayersMode.set(delegate, true)

  override def onItemPlace(guiElementScrollablePages: GuiElementScrollablePages, index: Int, mouseButton: Int): Unit = {
    symbolsH.set(symbolsH.get.patch(index, SelectedSymbol.get.fold(Seq.empty)(Seq(_)), 0))
    SelectedSymbol.clear()
  }

  override def onItemRemove(guiElementScrollablePages: GuiElementScrollablePages, index: Int): Unit = {
    SelectedSymbol.set(symbolsH.get.apply(index))
    symbolsH.set(symbolsH.get.patch(index, Seq.empty, 1))
  }

  override def getPageList: util.List[ItemStack] = symbolsToPages(symbolsH.get)

  override def getItemStack: ItemStack = ItemStack.EMPTY

  override def draw: Option[(Int, Int, Float) => Unit] = Some((mouseX, mouseY, partialTicks) => {
    delegate.renderBackground(partialTicks, mouseX, mouseY)
    val tooltip = delegate.getTooltipInfo()
    if (tooltip != null && tooltip.size > 0)
      drawHoveringText.apply(tooltip.get(0), mouseX, mouseY)
  })

  override def mouseInput: Option[(Int, Int) => Unit] = Some((_, _) => delegate.handleMouseInput())

  override def mouseClick: Option[(Int, Int, Int) => Unit] = Some(delegate.onMouseDown)

  override def mouseClickMove: Option[(Int, Int, Int) => Unit] = Some(delegate.onMouseDrag(_, _, _, 0))

  override def mouseRelease: Option[(Int, Int, Int) => Unit] = Some(delegate.onMouseUp)

  override def keyTyped: Option[(Char, Int) => Unit] = Some(delegate.onKeyPress)
}
