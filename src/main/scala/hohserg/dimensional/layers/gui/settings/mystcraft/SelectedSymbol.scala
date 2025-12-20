package hohserg.dimensional.layers.gui.settings.mystcraft

import com.xcompwiz.mystcraft.page.Page
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

object SelectedSymbol {

  private var symbol: Option[String] = None
  private var symbolPage: ItemStack = ItemStack.EMPTY

  def get: Option[String] = symbol

  def getPage: ItemStack = symbolPage

  def set(name: String): Unit = {
    symbol = Some(name)
    symbolPage = Page.createSymbolPage(new ResourceLocation(name))
  }

  def clear(): Unit = {
    symbol = None
    symbolPage = ItemStack.EMPTY
  }

}
