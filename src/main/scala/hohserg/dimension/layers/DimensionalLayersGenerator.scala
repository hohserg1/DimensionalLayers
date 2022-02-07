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
    //println("generateColumn", chunk.x, chunk.z)

  }

  override def populate(cube: ICube): Unit = {
    layers.get(cube.getY).foreach { case (startFrom, layer) =>
      val minY = cube.getY << 4
      val maxY = (cube.getY << 4) + 15
      val posToState = layer.getPopulatedChanges(cube.getX, cube.getZ)
      //println("populate1", startFrom, layer, minY, maxY, posToState.toString().substring(0,30))
      posToState
        .map { case (pos, state) => pos.up(startFrom << 4) -> state }
        .filter { case (pos, state) => minY <= pos.getY && pos.getY <= maxY }
        .foreach { case (pos, state) => cube.setBlockState(pos, state) }
    }
  }

  override def getFullPopulationRequirements(iCube: ICube): Box = {
    //println("getFullPopulationRequirements", iCube.getCoords)
    //ICubeGenerator.NO_REQUIREMENT
    new Box(-1, -1, -1, 0, 0, 0)
  }

  override def getPopulationPregenerationRequirements(iCube: ICube): Box = {
    //println("getPopulationPregenerationRequirements", iCube.getCoords)
    ICubeGenerator.NO_REQUIREMENT
  }

  override def recreateStructures(iCube: ICube): Unit = {
    println("recreateStructures", iCube.getCoords)

  }

  override def recreateStructures(chunk: Chunk): Unit = {
    println("recreateStructures", chunk.x, chunk.z)

  }

  override def getPossibleCreatures(enumCreatureType: EnumCreatureType, blockPos: BlockPos): util.List[Biome.SpawnListEntry] = {
    //println("getPossibleCreatures")
    ImmutableList.of()
  }

  override def getClosestStructure(s: String, blockPos: BlockPos, b: Boolean): BlockPos = {
    println("getClosestStructure")
    BlockPos.ORIGIN
  }
}
