package hohserg.dimensional.layers

import hohserg.dimensional.layers.gui.DrawableArea.Container
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.client.resources.I18n
import net.minecraft.world.{DimensionType, WorldType}
import org.lwjgl.opengl.GL11

package object gui {

  def makeDimensionTypeLabel(dimensionType: DimensionType): String =
    dimensionType.getName

  def makeWorldTypeLabel(worldType: WorldType): String =
    I18n.format("selectWorld.mapType") + " " + I18n.format(worldType.getTranslationKey)


}
