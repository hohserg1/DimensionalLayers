package hohserg.dimensional.layers

import hohserg.dimensional.layers.data.{LayerManager, LayerManagerServer}
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.worldgen.DimensionalLayersGenerator
import io.github.opencubicchunks.cubicchunks.api.util.IntRange
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import io.github.opencubicchunks.cubicchunks.api.worldgen.ICubeGenerator
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiCreateWorld
import net.minecraft.world.{World, WorldServer, WorldType}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object DimensionalLayersWorldType extends WorldType("dimlayers2") with ICubicWorldType {

  override def onGUICreateWorldPress(): Unit = {
  }

  override def createCubeGenerator(world: World): ICubeGenerator = {
    new DimensionalLayersGenerator(world.asInstanceOf[CCWorldServer])
  }

  override def calculateGenerationHeightRange(worldServer: WorldServer): IntRange = {
    LayerManagerServer.getWorldData(worldServer)
                      .map(data => IntRange.of(data.minBlockY, data.maxBlockY))
                      .getOrElse(IntRange.of(0, 0))
  }

  override def hasCubicGeneratorForWorld(world: World): Boolean = {
    LayerManager.getSided(world).haveWorldLayers(world)
  }

  override def isCustomizable: Boolean = true

  @SideOnly(Side.CLIENT)
  override def onCustomizeButton(mc: Minecraft, guiCreateWorld: GuiCreateWorld): Unit = {
    mc.displayGuiScreen(new GuiSetupDimensionalLayersPreset(guiCreateWorld))
  }
}
