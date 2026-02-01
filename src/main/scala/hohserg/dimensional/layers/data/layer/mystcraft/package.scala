package hohserg.dimensional.layers.data.layer

import com.xcompwiz.mystcraft.page.Page
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

import java.util
import scala.jdk.CollectionConverters.*

package object mystcraft {
  def symbolsToPages(symbols: Seq[String]): util.List[ItemStack] =
    symbols.map(pageOfSymbol).asJava

  def pageOfSymbol(symbol: String): ItemStack =
    Page.createSymbolPage(new ResourceLocation(symbol))
}
