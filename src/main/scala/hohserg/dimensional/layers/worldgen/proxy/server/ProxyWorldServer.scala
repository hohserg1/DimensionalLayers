package hohserg.dimensional.layers.worldgen.proxy.server

import hohserg.dimensional.layers.CCWorldServer
import hohserg.dimensional.layers.data.layer.base.{DimensionalLayer, Generator}
import hohserg.dimensional.layers.data.layer.cubic_world_type.{CubicWorldTypeGenerator, CubicWorldTypeLayer}
import hohserg.dimensional.layers.data.layer.vanilla_dimension.VanillaDimensionLayer
import hohserg.dimensional.layers.worldgen.proxy.{ProxyWorldCommon, ShiftedBlockPos}
import io.github.opencubicchunks.cubicchunks.api.world.ICubeProviderServer
import io.github.opencubicchunks.cubicchunks.api.worldgen.ICubeGenerator
import net.minecraft.entity.EnumCreatureType
import net.minecraft.profiler.Profiler
import net.minecraft.util.WeightedRandom
import net.minecraft.util.math.BlockPos
import net.minecraft.world._
import net.minecraft.world.biome.Biome
import net.minecraft.world.storage.WorldInfo

import java.util

object ProxyWorldServer {
  def apply(original: CCWorldServer, layer: VanillaDimensionLayer, generator: Generator): ProxyWorldServer = {
    new ProxyWorldServer(
      original,
      layer,
      generator,
      createLayerWorldInfo(original, layer.spec.seedOverride, layer.spec.worldType, layer.spec.worldTypePreset)
    )
  }

  def apply(original: CCWorldServer, layer: CubicWorldTypeLayer, generator: CubicWorldTypeGenerator): ProxyWorldServer = {
    new ProxyWorldServer(
      original,
      layer,
      generator,
      createLayerWorldInfo(original, layer.spec.seedOverride, layer.spec.cubicWorldType, layer.spec.worldTypePreset)
    )
  }

  def createLayerWorldInfo(original: World, seedOverride: Option[Long], worldType: WorldType, worldTypePreset: String): WorldInfo = {
    val originalWorldInfo = original.getWorldInfo
    val actualWorldInfo = new WorldInfo(originalWorldInfo)
    actualWorldInfo.populateFromWorldSettings(
      new WorldSettings(
        seedOverride.getOrElse(original.getSeed),
        originalWorldInfo.getGameType,
        originalWorldInfo.isMapFeaturesEnabled,
        originalWorldInfo.isHardcoreModeEnabled,
        worldType
      ).setGeneratorOptions(worldTypePreset)
    )
    actualWorldInfo
  }
}


class ProxyWorldServer private(val original: CCWorldServer, val layer: DimensionalLayer, generator: Generator, actualWorldInfo: WorldInfo)
  extends BaseWorldServer(
    new FakeSaveHandler(actualWorldInfo),
    actualWorldInfo,
    layer.dimensionType.createDimension(),
    new Profiler
  ) with FakeCubicWorldServer
    with ProxyWorldCommon {

  override type ProxyChunkProvider = ProxyChunkProviderServer

  override def createProxyChunkProvider(): ProxyChunkProviderServer = ProxyChunkProviderServer(this, original, layer)

  initWorld()

  initCapabilities()

  override def getCubeCache: ICubeProviderServer = proxyChunkProvider

  override def getSpawnListEntryForTypeAt(creatureType: EnumCreatureType, pos: BlockPos): Biome.SpawnListEntry = {
    val list = getPossibleCreatures(creatureType, pos)
    if (!list.isEmpty)
      WeightedRandom.getRandomItem(this.rand, list)
    else
      null
  }

  override def canCreatureTypeSpawnHere(creatureType: EnumCreatureType, spawnListEntry: Biome.SpawnListEntry, pos: BlockPos): Boolean =
    getPossibleCreatures(creatureType, pos).contains(spawnListEntry)

  private def getPossibleCreatures(creatureType: EnumCreatureType, pos: BlockPos): util.List[Biome.SpawnListEntry] =
    generator.getPossibleCreatures(creatureType, ShiftedBlockPos.unshift(pos))

  override def getCubeGenerator: ICubeGenerator =
    generator match {
      case generator: CubicWorldTypeGenerator => generator.generator
      case _ => null
    }

  getMapStorage
}