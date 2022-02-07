package hohserg.dimension.layers

import com.google.common.collect.ImmutableList
import io.github.opencubicchunks.cubicchunks.api.util.Box
import io.github.opencubicchunks.cubicchunks.api.world.ICube
import io.github.opencubicchunks.cubicchunks.api.worldgen.{CubePrimer, ICubeGenerator}
import net.minecraft.entity.EnumCreatureType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk

import java.util

class DimensionalLayersGenerator(world: World) extends ICubeGenerator {
  val preset = DimensionLayersPreset.fromJson(world.getWorldInfo.getGeneratorOptions)

  val layers: Map[Int, (Int, Layer)] =
    preset.toLayerMap
      .map { case (range, layerFactory) => range -> layerFactory(world) }
      .flatMap { case (range, layer) => for (i <- range.getMin to range.getMax) yield i -> (range.getMin -> layer) }
      .toMap

  override def generateCube(x: Int, y: Int, z: Int): CubePrimer = {
    //println("generateCube", x, y, z)
    val primer = new CubePrimer()

    layers.get(y).foreach { case (startFrom, layer) =>
      val blocks = layer.getChunkContentAt(x, z)
      val biomes = layer.getBiomeAt(x, z)

      for {
        i <- 0 to 15
        j <- 0 to 15
        k <- 0 to 15
      } primer.setBlockState(i, j, k, blocks(i, ((y - startFrom) << 4) + j, k))

      for {
        i <- 0 to 3
        k <- 0 to 3
      } {
        val biome = biomes(i * 4 + 1, k * 4 + 1)
        primer.setBiome(i, 0, k, biome)
      }
    }

    primer
  }

  override def generateColumn(chunk: Chunk): Unit = {
  }

  override def populate(cube: ICube): Unit = {}

  override def getFullPopulationRequirements(iCube: ICube): Box = {
    ICubeGenerator.NO_REQUIREMENT
  }

  override def getPopulationPregenerationRequirements(iCube: ICube): Box = {
    ICubeGenerator.NO_REQUIREMENT
  }

  override def recreateStructures(iCube: ICube): Unit = {
    println("recreateStructures", iCube.getCoords)

  }

  override def recreateStructures(chunk: Chunk): Unit = {
    println("recreateStructures", chunk.x, chunk.z)

  }

  override def getPossibleCreatures(enumCreatureType: EnumCreatureType, blockPos: BlockPos): util.List[Biome.SpawnListEntry] = {
    ImmutableList.of()
  }

  override def getClosestStructure(s: String, blockPos: BlockPos, b: Boolean): BlockPos = {
    println("getClosestStructure")
    BlockPos.ORIGIN
  }
}
