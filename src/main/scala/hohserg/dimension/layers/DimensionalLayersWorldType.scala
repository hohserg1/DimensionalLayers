package hohserg.dimension.layers

import hohserg.dimension.layers.gui.GuiSetupDimensionLayersPreset
import io.github.opencubicchunks.cubicchunks.api.util.IntRange
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import io.github.opencubicchunks.cubicchunks.api.worldgen.ICubeGenerator
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiCreateWorld
import net.minecraft.world.{World, WorldServer, WorldType}

class DimensionalLayersWorldType extends WorldType("dimension_layers") with ICubicWorldType {

  override def onGUICreateWorldPress(): Unit = {
  }

  override def createCubeGenerator(world: World): ICubeGenerator = {
    new DimensionalLayersGenerator(world)
  }

  override def calculateGenerationHeightRange(worldServer: WorldServer): IntRange = {
    val layers = DimensionLayersPreset.fromJson(worldServer.getWorldInfo.getGeneratorOptions).toLayerMap
    IntRange.of(
      layers.keys.minBy(_.getMin).getMin * 16,
      layers.keys.maxBy(_.getMax).getMax * 16 + 16
    )
  }

  override def hasCubicGeneratorForWorld(world: World): Boolean = {
    world.provider.getDimension == 0
  }

  override def isCustomizable: Boolean = true

  override def onCustomizeButton(mc: Minecraft, guiCreateWorld: GuiCreateWorld): Unit = {
    mc.displayGuiScreen(new GuiSetupDimensionLayersPreset(guiCreateWorld))
  }
}
