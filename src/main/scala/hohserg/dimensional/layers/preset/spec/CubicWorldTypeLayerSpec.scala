package hohserg.dimensional.layers.preset.spec

import hohserg.dimensional.layers.data.layer.cubic_world_type.CubicWorldTypeLayer
import hohserg.dimensional.layers.gui.preset.list.{GuiCubicWorldTypeLayerEntry, GuiLayerEntry, GuiLayersList}
import hohserg.dimensional.layers.worldgen.proxy.server.BaseWorldServer
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import net.minecraft.world._
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.storage.WorldInfo
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

case class CubicWorldTypeLayerSpec(cubicWorldType: WorldType with ICubicWorldType, worldTypePreset: String = "",
                                   dimensionType1: DimensionType = DimensionType.OVERWORLD,
                                   minCubeY: Int = 0,
                                   maxCubeY: Int = 32,
                                   seedOverride: Option[Long] = None
                                  ) extends LayerSpec {

  override val toLayer = CubicWorldTypeLayer

  override val height: Int = maxCubeY - minCubeY + 1


  @SideOnly(Side.CLIENT)
  override def toGuiLayerEntry(parent: GuiLayersList): GuiLayerEntry = new GuiCubicWorldTypeLayerEntry(parent, this)
}

object CubicWorldTypeLayerSpec {

  def dummyWorld(spec: CubicWorldTypeLayerSpec, gameType: GameType = GameType.SURVIVAL, isMapFeaturesEnabled: Boolean = true): WorldServer = {
    new BaseWorldServer(
      null,
      new WorldInfo(
        new WorldSettings(
          spec.seedOverride.getOrElse(0),
          gameType,
          isMapFeaturesEnabled,
          false,
          spec.cubicWorldType
        ).setGeneratorOptions(spec.worldTypePreset),
        "New World---"
      ),
      spec.dimensionType1.createDimension(),
      null
    ) {
      override def createChunkProvider(): IChunkProvider = ???

      override def isChunkLoaded(x: Int, z: Int, allowEmpty: Boolean): Boolean = ???
    }.asInstanceOf[WorldServer]
  }
}