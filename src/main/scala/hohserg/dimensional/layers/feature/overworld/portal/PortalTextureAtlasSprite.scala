package hohserg.dimensional.layers.feature.overworld.portal

import com.google.common.collect.ImmutableList
import hohserg.dimensional.layers.lens.TextureAtlasSpriteLens
import hohserg.dimensional.layers.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation

import java.util
import java.util.function

class PortalTextureAtlasSprite extends TextureAtlasSprite(Main.modid + ":blocks/portal") {
  private val vanillaPortalRL: ResourceLocation = new ResourceLocation("blocks/portal")
  override val getDependencies: util.Collection[ResourceLocation] = ImmutableList.of(vanillaPortalRL)

  override def hasCustomLoader(manager: IResourceManager, location: ResourceLocation): Boolean = true

  override def load(manager: IResourceManager, location: ResourceLocation, textureGetter: function.Function[ResourceLocation, TextureAtlasSprite]): Boolean = {
    val vanillaPortal = textureGetter.apply(vanillaPortalRL)

    width = vanillaPortal.getIconWidth
    height = vanillaPortal.getIconHeight

    clearFramesTextureData()

    for (i <- 0 until vanillaPortal.getFrameCount) {
      val pixels = new Array[Array[Int]](Minecraft.getMinecraft.gameSettings.mipmapLevels + 1)
      val first = new Array[Int](width * height)
      pixels(0) = first
      val vanillaPixels = vanillaPortal.getFrameTextureData(i)(0)
      for (p <- first.indices) {
        val color = vanillaPixels(p)
        val alpha = (color.getAlpha * 0.7).toInt
        first(p) = HueRotation.purpleToGreen.apply(color).withAlpha(alpha)
      }
      framesTextureData.add(pixels)
    }

    TextureAtlasSpriteLens.animationMetadata.set(this, TextureAtlasSpriteLens.animationMetadata.get(vanillaPortal))

    false
  }
}
