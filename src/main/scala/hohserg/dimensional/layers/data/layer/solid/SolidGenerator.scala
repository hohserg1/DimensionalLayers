package hohserg.dimensional.layers.data.layer.solid

import hohserg.dimensional.layers.data.layer.base.Generator
import io.github.opencubicchunks.cubicchunks.api.world.ICube
import io.github.opencubicchunks.cubicchunks.api.worldgen.CubePrimer
import net.minecraft.entity.EnumCreatureType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome

import java.util

class SolidGenerator(val layer: SolidLayer) extends Generator {
  override type L = SolidLayer
  override type BiomeContext = Biome

  override def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer): CubePrimer = {
    for {
      x <- 0 to 15
      y <- 0 to 15
      z <- 0 to 15
    } primer.setBlockState(x, y, z, spec.filler)
    generateBiomes(primer, spec.biome)
    primer
  }

  override protected def calcBiome(localBiomeX: Int, localBiomeY: Int, localBiomeZ: Int, context: Biome): Biome = context

  override def populateCube(cube: ICube): Unit = ()

  override def getPossibleCreaturesNullable(creatureType: EnumCreatureType, realPos: BlockPos): util.List[Biome.SpawnListEntry] = null

  override def recreateStructures(cube: ICube): Unit = ()
}
