package hohserg.dimensional.layers.gui.settings.additional.features

import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.settings.additional.features.GuiPotionList.DrawablePotion
import hohserg.dimensional.layers.gui.{DrawableArea, GuiScrollingListElement, drawWithTexture}
import hohserg.dimensional.layers.preset.spec.{AdditionalFeature, BlockReplacing, PotionEffectGranting}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.{GlStateManager, RenderHelper, Tessellator}
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack

class AdditionalFeaturesList(x: Int, y: Int, w: Int, h: Int, holder: ValueHolder[Seq[AdditionalFeature]]) extends GuiScrollingListElement(x, y, w, h, entryHeight = 18 + 4) {

  override def getSize: Int = holder.get.size

  override def elementClicked(i: Int, bl: Boolean): Unit = {

  }

  override def isSelected(i: Int): Boolean = false

  override def drawBackground(): Unit = ()

  val arrowContainer = new DrawableArea.MutableContainer

  override def drawSlot(index: Int, right: Int, top: Int, height: Int, tess: Tessellator): Unit = {
    val features = holder.get
    if (features.indices contains index) {
      val renderItem = Minecraft.getMinecraft.getRenderItem
      features.apply(index) match {
        case BlockReplacing(from, to) =>
          GlStateManager.color(1, 1, 1, 1)
          RenderHelper.enableGUIStandardItemLighting()
          renderItem.renderItemIntoGUI(new ItemStack(from.getBlock), left + 5, top + 1)
          renderItem.renderItemIntoGUI(new ItemStack(to.getBlock), left + 2 + 18 + 15 + 1, top + 1)
          RenderHelper.disableStandardItemLighting()
          arrowContainer.minX = left
          arrowContainer.maxX = right
          arrowContainer.minY = top
          arrowContainer.maxY = top + height
          drawWithTexture(texture, arrowArea.draw(_)(using arrowContainer))
        case PotionEffectGranting(effect, amplifier, playerOnly) =>
          GlStateManager.color(1, 1, 1, 1)
          val x = left + 10
          DrawablePotion(effect).draw(x, top + 1, x + height, top + 1 + height)
          Minecraft.getMinecraft.fontRenderer.drawStringWithShadow(I18n.format("enchantment.level." + (amplifier + 1)), left + 7 + height + 2, top + 4, 0xff_FF00FF)
      }
    }
  }
}
