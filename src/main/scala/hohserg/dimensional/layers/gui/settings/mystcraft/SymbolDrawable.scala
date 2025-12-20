package hohserg.dimensional.layers.gui.settings.mystcraft

import com.xcompwiz.mystcraft.client.gui.GuiUtils
import com.xcompwiz.mystcraft.page.Page
import com.xcompwiz.mystcraft.symbol.SymbolManager
import hohserg.dimensional.layers.gui.Drawable
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

case class SymbolDrawable(name: String) extends Drawable {

  private val stack = Page.createSymbolPage(new ResourceLocation(name))

  override def draw(minX: Int, minY: Int, maxX: Int, maxY: Int): Unit =
    GuiUtils.drawPage(Minecraft.getMinecraft.getTextureManager,
      0,
      stack,
      maxX - minX, maxY - minY,
      minX, minY
    )

  override lazy val tooltip: String = 
    SymbolManager.getAgeSymbol(new ResourceLocation(name)).getLocalizedName
}
