package hohserg.dimensional.layers.preset.spec

import hohserg.dimensional.layers.CCWorld
import hohserg.dimensional.layers.data.layer.cubic_world_type.CubicWorldTypeLayer
import hohserg.dimensional.layers.gui.preset.list.{GuiCubicWorldTypeLayerEntry, GuiLayerEntry, GuiLayersList}
import hohserg.dimensional.layers.preset.spec.CubicWorldTypeLayerSpec.dummyWorld
import hohserg.dimensional.layers.worldgen.proxy.server.BaseWorldServer
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.storage.WorldInfo
import net.minecraft.world._
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

case class CubicWorldTypeLayerSpec(cubicWorldType: WorldType with ICubicWorldType, worldTypePreset: String = "",
                                   dimensionType1: DimensionType = DimensionType.OVERWORLD,
                                   seedOverride: Option[Long] = None
                                  ) extends LayerSpec {

  def rangeCube(originalWorld: CCWorld): (Int, Int) =
    rangeCube(originalWorld.getWorldInfo.getGameType, originalWorld.getWorldInfo.isMapFeaturesEnabled)

  def rangeCube(gameType: GameType, isMapFeaturesEnabled: Boolean): (Int, Int) = {
    val range1 = cubicWorldType.calculateGenerationHeightRange(
      dummyWorld(this, gameType, isMapFeaturesEnabled)
    )
    Coords.blockToCube(range1.getMin) -> Coords.blockToCube(range1.getMax)
  }

  override val toLayer = CubicWorldTypeLayer

  override val height: Int = {
    val (virtualStartCubeY, virtualEndCubeY) = rangeCube(GameType.SURVIVAL, isMapFeaturesEnabled = true)
    virtualEndCubeY - virtualStartCubeY + 1
  }

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