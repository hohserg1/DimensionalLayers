package hohserg.dimensional.layers.data.layer.cubic_world_type

import hohserg.dimensional.layers.CCWorldServer
import hohserg.dimensional.layers.data.layer.base.DimensionalGenerator
import hohserg.dimensional.layers.worldgen.proxy.ProxyCube
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer.createLayerWorldInfo
import io.github.opencubicchunks.cubicchunks.api.world.ICube
import io.github.opencubicchunks.cubicchunks.api.worldgen.CubePrimer
import net.minecraft.entity.EnumCreatureType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome

import java.util

class CubicWorldTypeGenerator(original: CCWorldServer, val layer: CubicWorldTypeLayer) extends DimensionalGenerator {
  override type L = CubicWorldTypeLayer

  override val proxyWorld = new ProxyWorldServer(
    original,
    layer,
    this,
    createLayerWorldInfo(original, layer.spec.seedOverride, layer.spec.cubicWorldType, layer.spec.worldTypePreset)
  )

  val generator = spec.cubicWorldType.createCubeGenerator(proxyWorld)

  override def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer): CubePrimer =
    generator.generateCube(cubeX, cubeY, cubeZ, primer)

  override def populateCube(cube: ICube): Unit =
    generator.populate(new ProxyCube(cube, bounds, proxyWorld))

  override def getPossibleCreaturesNullable(creatureType: EnumCreatureType, realPos: BlockPos): util.List[Biome.SpawnListEntry] =
    generator.getPossibleCreatures(creatureType, realPos)

  override def recreateStructures(cube: ICube): Unit =
    generator.recreateStructures(cube)
}
