package hohserg.dimensional.layers.gui.settings.additional.features

import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.preset.list.{background, texture as presetTexture}
import hohserg.dimensional.layers.gui.settings.additional.features.GuiPotionList.DrawablePotion
import hohserg.dimensional.layers.gui.{DrawableArea, GuiScrollingListElement, drawWithTexture}
import hohserg.dimensional.layers.preset.spec.{AdditionalFeature, BlockReplacing, PotionEffectGranting}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.{GlStateManager, RenderHelper, Tessellator}
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack

class AdditionalFeaturesList(x: Int, y: Int, w: Int, h: Int, holder: ValueHolder[Seq[AdditionalFeature]]) extends GuiScrollingListElement(x, y, w, h, entryHeight = 18 + 4) {

  override def getSize: Int = holder.get.size

  implicit val entryContainer: DrawableArea.MutableContainer = new DrawableArea.MutableContainer

  override def elementClicked(index: Int, doubleClick: Boolean): Unit = {
    val features = holder.get
    if (features.indices contains index) {
      if (remove.isHovering) {
        holder.set(features.patch(index, Seq.empty, 1))
      }
    }
  }

  override def isSelected(i: Int): Boolean = false

  override def drawBackground(): Unit = ()

  override def drawSlot(index: Int, right: Int, top: Int, height: Int, tess: Tessellator): Unit = {
    val features = holder.get
    if (features.indices contains index) {
      entryContainer.minX = left
      entryContainer.maxX = right
      entryContainer.minY = top
      entryContainer.maxY = top + height
      val renderItem = Minecraft.getMinecraft.getRenderItem
      features.apply(index) match {
        case BlockReplacing(from, to) =>
          GlStateManager.color(1, 1, 1, 1)
          RenderHelper.enableGUIStandardItemLighting()
          renderItem.renderItemIntoGUI(new ItemStack(from.getBlock), left + 3, top + 1)
          renderItem.renderItemIntoGUI(new ItemStack(to.getBlock), left + 25, top + 1)
          RenderHelper.disableStandardItemLighting()
          drawWithTexture(texture, arrowArea.draw(_))
        case PotionEffectGranting(effect, amplifier, playerOnly) =>
          GlStateManager.color(1, 1, 1, 1)
          val x = left + 12
          DrawablePotion(effect).draw(x, top + 1, x + height, top + 1 + height)
          Minecraft.getMinecraft.fontRenderer.drawStringWithShadow(I18n.format("enchantment.level." + (amplifier + 1)), left + 7 + height + 4, top + 5, 0xff_FF00FF)
      }
      if (background.isHovering)
        drawWithTexture(presetTexture, remove.draw(_))
    }
  }
}
