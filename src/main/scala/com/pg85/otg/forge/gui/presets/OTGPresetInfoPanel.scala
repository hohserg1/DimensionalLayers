package com.pg85.otg.forge.gui.presets

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.pg85.otg.configuration.dimensions.DimensionConfigGui
import com.pg85.otg.forge.gui.GuiHandler
import com.pg85.otg.forge.gui.presets.OTGPresetInfoPanel.{Logo, logoCache}
import hohserg.dimensional.layers._
import hohserg.dimensional.layers.gui.GuiElement
import hohserg.dimensional.layers.gui.add._
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.{Loader, ModMetadata}

import java.awt.Dimension
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import scala.collection.JavaConverters._
import scala.util.Try

class OTGPresetInfoPanel(parent: otg.GuiAddLayer) extends GuiElement {
  private val fakeParent = new OTGGuiPresetList(null, true)
  fakeParent.setWorldAndResolution(Minecraft.getMinecraft, parent.width, parent.height)
  fakeParent.bottomMargin = fakeParent.topMargin
  private val contentByPresetName: Map[String, OTGGuiScrollingListInfo] =
    GuiHandler.GuiPresets.asScala.toMap.map { case (presetName: String, presetData: DimensionConfigGui) =>
      val maybeMod = Option(presetData.worldPackerModName)
        .mapNull(Loader.instance().getIndexedModList.get)
        .mapNull(_.getMetadata)

      val lines = {
        List(
          "Name: " + presetName,
          "Version: " + maybeMod.mapNull(_.version).getOrElse("Unknown"),
          "Credits: " + maybeMod.mapNull(_.credits).getOrElse(presetData.author)
        ) ++
          maybeMod.mapNull(_.url).map("URL: " + _) :+
          maybeMod.mapNull(_.description).getOrElse(presetData.description)
      }.asJava

      val (logo, logoDim) = maybeMod.flatMap(logoCache.get) match {
        case Some(Logo(texture, w, h)) => (texture, new Dimension(w, h))
        case None => (null, new Dimension(0, 0))
      }

      presetName -> new OTGGuiScrollingListInfo(
        fakeParent,
        lines,
        logo, logoDim
      )
    }

  override def draw: Option[(Int, Int, Float) => Unit] = Some(
    (mx, my, partialTicks) =>
      parent.hoveringPreset.flatMap(contentByPresetName.get) match {
        case Some(x) => x.drawScreen(mx, my, partialTicks, 1)
        case None =>
      }
  )

}

object OTGPresetInfoPanel {

  case class Logo(texture: ResourceLocation, w: Int, h: Int)

  private def createDynamicTexture(image: BufferedImage): ResourceLocation =
    Minecraft.getMinecraft.getTextureManager.getDynamicTextureLocation("modlogo", new DynamicTexture(image))

  val logoCache: LoadingCache[ModMetadata, Option[Logo]] = CacheBuilder.newBuilder().build(new CacheLoader[ModMetadata, Option[Logo]] {
    override def load(mod: ModMetadata): Option[Logo] = {
      Try(
        Option(mod)
          .map(_.modId)
          .mapNull(FMLClientHandler.instance.getResourcePackFor)
          .mapNull(_.getPackImage)
          .orElse(
            Option(mod)
              .mapNull(_.logoFile)
              .filter(_.nonEmpty)
              .mapNull(this.getClass.getResourceAsStream)
              .map(ImageIO.read)
          )
          .map(i => Logo(createDynamicTexture(i), i.getWidth, i.getHeight))
      ).toOption.flatten
    }
  })

}
