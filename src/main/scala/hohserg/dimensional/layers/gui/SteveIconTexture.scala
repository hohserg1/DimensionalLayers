package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.*
import net.minecraft.client.renderer.texture.{SimpleTexture, TextureUtil}
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation

import java.awt.image.BufferedImage

val steveIconRL = new ResourceLocation(Main.modid, "textures/gui/steve.png")

class SteveIconTexture extends SimpleTexture(steveIconRL) {
  val vanillaSkinRL = new ResourceLocation("minecraft", "textures/entity/steve.png")

  override def loadTexture(resourceManager: IResourceManager): Unit = {
    this.deleteGlTexture()
    val resource = resourceManager.getResource(vanillaSkinRL)
    val sourcePixels = TextureUtil.readBufferedImage(resource.getInputStream)
    val sourceW = sourcePixels.getWidth

    val fromUVSize = 64
    val toUVSize = 36

    val scale = sourceW / fromUVSize
    val targetW = toUVSize * scale
    val targetPixels = new BufferedImage(targetW, targetW, BufferedImage.TYPE_INT_ARGB)

    def copyPixels(from: (Int, Int, Int, Int), to: (Int, Int), alphaFactor: Double = 1): Unit = {
      val from_u1 = from._1 * sourceW / fromUVSize
      val from_v1 = from._2 * sourceW / fromUVSize
      val w = from._3 * sourceW / fromUVSize
      val h = from._4 * sourceW / fromUVSize

      val to_u1 = to._1 * targetW / toUVSize
      val to_v1 = to._2 * targetW / toUVSize

      for {
        lu <- 0 until w
        lv <- 0 until h
      } {
        val from_u = from_u1 + lu
        val from_v = from_v1 + lv
        val to_u = to_u1 + lu
        val to_v = to_v1 + lv

        val color = sourcePixels.getRGB(from_u, from_v)
        targetPixels.setRGB(to_u, to_v, color.withAlpha((color.getAlpha * alphaFactor).toInt))
      }
    }

    def makeSolidTexture(): Unit = {
      copyPixels(from = (8, 8, 8, 8), to = (5, 1))

      copyPixels(from = (20, 20, 8, 12), to = (5, 9))

      copyPixels(from = (44, 20, 4, 12), to = (1, 9))
      copyPixels(from = (36, 52, 4, 12), to = (13, 9))

      copyPixels(from = (20, 52, 4, 12), to = (5, 21))
      copyPixels(from = (4, 20, 4, 12), to = (9, 21))
    }

    def makeTranslucentTexture(): Unit = {
      def copyPixels2(from: (Int, Int, Int, Int), to: (Int, Int)): Unit = {
        copyPixels(from, to, alphaFactor = 171 / 255d)
      }

      copyPixels2(from = (8, 8, 8, 8), to = (18 + 5, 1))

      copyPixels2(from = (20, 20, 8, 12), to = (18 + 5, 9))

      copyPixels2(from = (44, 20, 4, 12), to = (18 + 1, 9))
      copyPixels2(from = (36, 52, 4, 12), to = (18 + 13, 9))

      copyPixels2(from = (20, 52, 4, 12), to = (18 + 5, 21))
      copyPixels2(from = (4, 20, 4, 12), to = (18 + 9, 21))
    }

    makeSolidTexture()
    makeTranslucentTexture()

    TextureUtil.uploadTextureImageAllocate(this.getGlTextureId, targetPixels, false, false)
  }
}