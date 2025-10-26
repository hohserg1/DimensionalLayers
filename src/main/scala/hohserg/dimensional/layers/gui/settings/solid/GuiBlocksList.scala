package hohserg.dimensional.layers.gui.settings.solid

import com.google.common.cache.LoadingCache
import hohserg.dimensional.layers.gui.GuiTileList.SelectHandler
import hohserg.dimensional.layers.gui.settings.solid.GuiBlocksList.{blockLinesByLen, itemWidth}
import hohserg.dimensional.layers.gui.{Drawable, GuiTileList}
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.{GlStateManager, RenderHelper}
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.jdk.CollectionConverters.*


@SideOnly(Side.CLIENT)
object GuiBlocksList {
  val itemWidth = 18
  lazy val allBlocks: Seq[DrawableBlock] =
    DrawableBlock(Blocks.AIR) +:
      ForgeRegistries.BLOCKS.getValuesCollection.asScala
                     .filter(Item.getItemFromBlock(_) != Items.AIR)
                     .map(DrawableBlock.apply)
                     .toIndexedSeq

  val blockLinesByLen: LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[DrawableBlock]]] = GuiTileList.createLinesCache(allBlocks, itemWidth)

  case class DrawableBlock(block: Block) extends Drawable {
    val stack = new ItemStack(block)

    override def draw(minX: Int, minY: Int, maxX: Int, maxY: Int): Unit = {
      Minecraft.getMinecraft.getRenderItem.renderItemIntoGUI(stack, minX + 1, minY + 1)
    }

    override def tooltip: String = stack.getDisplayName
  }
}

@SideOnly(Side.CLIENT)
class GuiBlocksList(parent: SelectHandler[GuiBlocksList.DrawableBlock], x: Int, y: Int, w: Int, h: Int)
  extends GuiTileList(parent, x, y, w, h, itemWidth, blockLinesByLen)() {
  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    RenderHelper.enableGUIStandardItemLighting()
    super.drawScreen(mouseX, mouseY, partialTicks)
    RenderHelper.disableStandardItemLighting()
  }
}
