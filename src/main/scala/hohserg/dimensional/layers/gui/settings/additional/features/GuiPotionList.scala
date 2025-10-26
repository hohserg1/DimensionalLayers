package hohserg.dimensional.layers.gui.settings.additional.features

import com.google.common.cache.LoadingCache
import hohserg.dimensional.layers.gui.GuiTileList.SelectHandler
import hohserg.dimensional.layers.gui.settings.additional.features.GuiPotionList.{DrawablePotion, potionLinesByLen}
import hohserg.dimensional.layers.gui.settings.solid.GuiBlocksList.itemWidth
import hohserg.dimensional.layers.gui.{Drawable, GuiTileList, drawTexturedRect}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.resources.I18n
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.Potion
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.common.registry.ForgeRegistries

import scala.jdk.CollectionConverters.*

object GuiPotionList {

  lazy val allPotions: Seq[DrawablePotion] =
    ForgeRegistries.POTIONS.getValuesCollection.asScala
                   .map(DrawablePotion.apply)
                   .toIndexedSeq

  val potionLinesByLen: LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[DrawablePotion]]] = GuiTileList.createLinesCache(allPotions, itemWidth)

  case class DrawablePotion(p: Potion) extends Drawable {
    private val item = makePotionStack(p)

    override def draw(minX: Int, minY: Int, maxX: Int, maxY: Int): Unit = {
      if (p.hasStatusIcon) {
        val i1 = p.getStatusIconIndex
        drawTexturedRect(minX, minY, maxX, maxY, GuiContainer.INVENTORY_BACKGROUND, (i1 % 8 * 18) / 255d, (198 + i1 / 8 * 18) / 255d, (i1 % 8 * 18 + 18) / 255d, (198 + i1 / 8 * 18 + 18) / 255d)
      } else {
        Minecraft.getMinecraft.getRenderItem.renderItemIntoGUI(item, minX, minY)
      }
    }

    override def tooltip: String = (if (p.isBadEffect) TextFormatting.RED else TextFormatting.BLUE).toString + I18n.format(p.getName)
  }

  def makePotionStack(p: Potion): ItemStack = {
    val item = new ItemStack(Items.POTIONITEM)
    item.setTagCompound(new NBTTagCompound)
    item.getTagCompound.setString("Potion", p.getRegistryName.toString)
    item
  }

}

class GuiPotionList(parent: SelectHandler[DrawablePotion], x: Int, y: Int, w: Int, h: Int)
  extends GuiTileList(parent, x, y, w, h, itemWidth, potionLinesByLen)() {
  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    RenderHelper.enableGUIStandardItemLighting()
    super.drawScreen(mouseX, mouseY, partialTicks)
    RenderHelper.disableStandardItemLighting()
  }
}
