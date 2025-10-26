package hohserg.dimensional.layers.data.layer.cubic_world_type

import hohserg.dimensional.layers.CCWorldServer
import hohserg.dimensional.layers.data.layer.base.DimensionalGenerator
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer.createLayerWorldInfo
import hohserg.dimensional.layers.worldgen.proxy.{ProxyCube, ShiftedBlockPos}
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

  val generator = layer.spec.cubicWorldType.createCubeGenerator(proxyWorld)

  override def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer): CubePrimer = {
    val result = generator.generateCube(cubeX, ShiftedBlockPos.unshiftCubeY(cubeY, layer.bounds), cubeZ, primer)
    if (layer.hasBlockReplacing) {
      for {
        x <- 0 to 15
        y <- 0 to 15
        z <- 0 to 15
        block = result.getBlockState(x, y, z)
        block2 <- layer.blockReplacements.get(block)
      } primer.setBlockState(x, y, z, block2)
    }
    result
  }

  override def populateCube(cube: ICube): Unit = {
    generator.populate(new ProxyCube(cube, layer.bounds, proxyWorld))
  }

  override def getPossibleCreaturesNullable(creatureType: EnumCreatureType, realPos: BlockPos): util.List[Biome.SpawnListEntry] = {
    generator.getPossibleCreatures(creatureType, realPos)
  }

  override def recreateStructures(cube: ICube): Unit = {
    generator.recreateStructures(cube)
  }
}
