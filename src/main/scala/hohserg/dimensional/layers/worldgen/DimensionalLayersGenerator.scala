package hohserg.dimensional.layers.worldgen

import com.google.common.collect.ImmutableList
import hohserg.dimensional.layers.data.LayerManagerServer
import hohserg.dimensional.layers.data.layer.base.Layer
import hohserg.dimensional.layers.{CCWorldServer, Main}
import io.github.opencubicchunks.cubicchunks.api.util.{Box, Coords}
import io.github.opencubicchunks.cubicchunks.api.world.{ICube, ICubicWorld}
import io.github.opencubicchunks.cubicchunks.api.worldgen.{CubePrimer, ICubeGenerator}
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube
import io.github.opencubicchunks.cubicchunks.core.worldgen.WorldgenHangWatchdog
import net.minecraft.entity.EnumCreatureType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk

import java.{util => ju}
import scala.collection.JavaConverters._
import scala.collection.mutable

class DimensionalLayersGenerator(original: CCWorldServer) extends ICubeGenerator {
  val worldData = LayerManagerServer.getWorldData(original).getOrElse(throw new IllegalArgumentException("not a layered world: " + original))
  val preset = worldData.preset
  val layerAtCubeY: Map[Int, Layer] = worldData.layerAtCubeY

  val optimizationHack: mutable.Map[Layer, Boolean] = new ju.IdentityHashMap[Layer, Boolean]().asScala.withDefaultValue(false)

  private def generateWithWatchdog[BlockStateAcceptor, Result](generator: (Int, Int, Int, BlockStateAcceptor) => Result,
                                                               cubeX: Int, cubeY: Int, cubeZ: Int,
                                                               target: BlockStateAcceptor): Option[Result] = {
    try {
      WorldgenHangWatchdog.startWorldGen()
      Some(generator(cubeX, cubeY, cubeZ, target))
    } catch {
      case e: Throwable =>
        Main.sided.printError("Generation issue:", e)
        None
    } finally {
      WorldgenHangWatchdog.endWorldGen()
    }
  }

  override def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int, primer: CubePrimer): CubePrimer = {
    layerAtCubeY.get(cubeY).flatMap { layer =>
      if (layer.generator.needGenerateTotalColumn)
        if (!optimizationHack(layer)) {
          optimizationHack(layer) = true
          recursiveGeneration(cubeX, cubeY, cubeZ, layer)
          optimizationHack(layer) = false
        }

      generateWithWatchdog(layer.generator.generateCube, cubeX, cubeY, cubeZ, primer)

    }.getOrElse(primer)
  }

  private def recursiveGeneration(cubeX: Int, cubeY: Int, cubeZ: Int, layer: Layer): Unit = {
    for (y <- (layer.bounds.realStartCubeY + layer.bounds.cubeHeight - 1) to layer.bounds.realStartCubeY by -1)
      if (y != cubeY)
        original.asInstanceOf[ICubicWorld].getCubeFromCubeCoords(cubeX, y, cubeZ)
  }

  override def populate(cube: ICube): Unit = {
    layerAtCubeY.get(cube.getY).foreach {
      layer =>
        if (layer.generator.needGenerateTotalColumn)
          markColumnPopulated(cube.getX, cube.getZ, layer)

        generateWithWatchdog[ICube, Unit]((_, _, _, c) => layer.generator.populateCube(c), cube.getX, cube.getY, cube.getZ, cube)
    }
  }

  private def markColumnPopulated(cubeX: Int, cubeZ: Int, layer: Layer): Unit = {
    for (y <- (layer.bounds.realStartCubeY + layer.bounds.cubeHeight - 1) to layer.bounds.realStartCubeY by -1) {
      original.getCubeFromCubeCoords(cubeX, y, cubeZ).asInstanceOf[Cube].setPopulated(true)
    }
  }


  override def getFullPopulationRequirements(cube: ICube): Box = {
    layerAtCubeY.get(cube.getY).collect {
      case layer if layer.generator.needGenerateTotalColumn =>
        val i = cube.getY - layer.bounds.realStartCubeY
        new Box(-1, -i, -1, 0, layer.bounds.cubeHeight - i - 1, 0)
    }.getOrElse(ICubeGenerator.NO_REQUIREMENT)
  }

  override def getPopulationPregenerationRequirements(cube: ICube): Box = {
    layerAtCubeY.get(cube.getY).collect {
      case layer if layer.generator.needGenerateTotalColumn =>
        val i = cube.getY - layer.bounds.realStartCubeY
        new Box(0, -i, 0, 1, layer.bounds.cubeHeight - i - 1, 1)
    }.getOrElse(ICubeGenerator.NO_REQUIREMENT)
  }

  override def generateColumn(chunk: Chunk): Unit = ()

  override def recreateStructures(cube: ICube): Unit =
    layerAtCubeY.get(cube.getY).foreach {
      layer =>
        layer.generator.recreateStructures(cube)
    }

  override def recreateStructures(chunk: Chunk): Unit = ()

  override def getPossibleCreatures(enumCreatureType: EnumCreatureType, blockPos: BlockPos): ju.List[Biome.SpawnListEntry] =
    layerAtCubeY.get(Coords.blockToCube(blockPos.getY))
      .map(layer => layer.generator.getPossibleCreatures(enumCreatureType, blockPos))
      .getOrElse(ImmutableList.of())

  override def getClosestStructure(name: String, blockPos: BlockPos, findUnexplored: Boolean): BlockPos =
    layerAtCubeY.get(Coords.blockToCube(blockPos.getY))
      .flatMap(layer => layer.generator.getNearestStructurePos(name, blockPos, findUnexplored))
      .orNull

  override def generateCube(cubeX: Int, cubeY: Int, cubeZ: Int): CubePrimer = generateCube(cubeX, cubeY, cubeZ, new CubePrimer())
}
