package hohserg.dimensional.layers.data.layer.base

import com.google.common.collect.ImmutableList
import hohserg.dimensional.layers.preset.spec.LayerSpec
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer
import io.github.opencubicchunks.cubicchunks.api.world.ICube
import io.github.opencubicchunks.cubicchunks.api.worldgen.CubePrimer
import net.minecraft.entity.EnumCreatureType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome

import java.util

trait Generator {
  type L <: Layer

  def layer: L

  def needGenerateTotalColumn: Boolean = false

  def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer): CubePrimer

  def populateCube(cube: ICube): Unit

  def recreateStructures(cube: ICube): Unit

  def getPossibleCreatures(creatureType: EnumCreatureType, realPos: BlockPos): util.List[Biome.SpawnListEntry] = {
    Option(getPossibleCreaturesNullable(creatureType, realPos)).getOrElse(ImmutableList.of())
  }

  def getPossibleCreaturesNullable(creatureType: EnumCreatureType, realPos: BlockPos): util.List[Biome.SpawnListEntry]

  def getNearestStructurePos(name: String, blockPos: BlockPos, findUnexplored: Boolean): Option[BlockPos] = None

}

trait DimensionalGenerator extends Generator {
  override type L <: DimensionalLayer

  def proxyWorld: ProxyWorldServer
}

trait BiomeGeneratorHelper {
  type BiomeContext

  protected def calcBiome(localBiomeX: Int, localBiomeY: Int, localBiomeZ: Int, context: BiomeContext): Biome

  protected def generateBiomes(primer: CubePrimer, context: BiomeContext): Unit = {
    for {
      localBiomeX <- 0 to 3
      localBiomeY <- 0 to 3
      localBiomeZ <- 0 to 3
    } primer.setBiome(localBiomeX, localBiomeY, localBiomeZ, calcBiome(localBiomeX, localBiomeY, localBiomeZ, context))
  }

}