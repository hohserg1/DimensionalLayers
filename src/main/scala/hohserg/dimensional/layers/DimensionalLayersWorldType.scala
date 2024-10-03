package hohserg.dimensional.layers

import hohserg.dimensional.layers.data.layer.base.Layer
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.preset.DimensionalLayersPreset
import hohserg.dimensional.layers.worldgen.DimensionalLayersGenerator
import hohserg.dimensional.layers.worldgen.proxy.server.BaseWorldServer
import io.github.opencubicchunks.cubicchunks.api.util.IntRange
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import io.github.opencubicchunks.cubicchunks.api.worldgen.ICubeGenerator
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiCreateWorld
import net.minecraft.world.{World, WorldServer, WorldType}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object DimensionalLayersWorldType extends WorldType("dimlayers") with ICubicWorldType {

  override def onGUICreateWorldPress(): Unit = {
  }

  override def createCubeGenerator(world: World): ICubeGenerator = {
    new DimensionalLayersGenerator(world.asInstanceOf[CCWorldServer])
  }

  override def calculateGenerationHeightRange(worldServer: WorldServer): IntRange = {
    val layers: Seq[(IntRange, Layer)] = DimensionalLayersPreset.fromJson(worldServer.getWorldInfo.getGeneratorOptions).toLayerSeq(worldServer.asInstanceOf[CCWorldServer])
    IntRange.of(
      layers.minBy(_._1.getMin)._1.getMin * 16,
      layers.maxBy(_._1.getMax)._1.getMax * 16 + 16
    )
  }

  override def hasCubicGeneratorForWorld(world: World): Boolean = {
    world.provider.getDimension == 0 && !world.isInstanceOf[BaseWorldServer]
  }

  override def isCustomizable: Boolean = true

  @SideOnly(Side.CLIENT)
  override def onCustomizeButton(mc: Minecraft, guiCreateWorld: GuiCreateWorld): Unit = {
    mc.displayGuiScreen(new GuiSetupDimensionalLayersPreset(guiCreateWorld))
  }
}
