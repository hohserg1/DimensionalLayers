package hohserg.dimensional.layers.data.layer.cubic_world_type

import hohserg.dimensional.layers.CCWorldServer
import hohserg.dimensional.layers.data.layer.base.Generator
import hohserg.dimensional.layers.worldgen.proxy.ProxyCube
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer
import io.github.opencubicchunks.cubicchunks.api.world.ICube
import io.github.opencubicchunks.cubicchunks.api.worldgen.CubePrimer
import net.minecraft.entity.EnumCreatureType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome

import java.util

class CubicWorldTypeGenerator(original: CCWorldServer, val layer: CubicWorldTypeLayer) extends Generator {
  override type L = CubicWorldTypeLayer
  override type BiomeContext = Nothing

  val proxyWorld = ProxyWorldServer(original, layer, this)

  val generator = spec.cubicWorldType.createCubeGenerator(proxyWorld)

  override def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer): CubePrimer =
    generator.generateCube(cubeX, cubeY, cubeZ, primer)

  override protected def calcBiome(localBiomeX: Int, localBiomeY: Int, localBiomeZ: Int, context: Nothing): Biome = ???

  override def populateCube(cube: ICube): Unit =
    generator.populate(new ProxyCube(cube, bounds, proxyWorld))

  override def getPossibleCreaturesNullable(creatureType: EnumCreatureType, realPos: BlockPos): util.List[Biome.SpawnListEntry] =
    generator.getPossibleCreatures(creatureType, realPos)

  override def recreateStructures(cube: ICube): Unit =
    generator.recreateStructures(cube)
}
