package hohserg.dimensional.layers.feature

import gloomyfolken.hooklib.api.*
import hohserg.dimensional.layers.*
import hohserg.dimensional.layers.data.layer.base.DimensionalLayer
import hohserg.dimensional.layers.data.layer.solid.SolidLayer
import hohserg.dimensional.layers.data.{LayerManager, WorldData}
import io.github.opencubicchunks.cubicchunks.core.world.SpawnPlaceFinder
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockPos.MutableBlockPos
import net.minecraft.world.{World, WorldServer}

import scala.util.boundary
import scala.util.boundary.break

@HookContainer
object SpawnInLayer {

  @Hook
  @OnBegin
  def getRandomizedSpawnPoint(finder: SpawnPlaceFinder, world: World): ReturnSolve[BlockPos] = {
    if (Configuration.spawnInLayer) {
      LayerManager.getSided(world).getWorldData(world) match {
        case Some(data) =>
          val (x, z) = getHorizontalPos(data, world)
          val minY = data.minSpawnBlockY
          val maxY = data.maxSpawnBlockY
          val center = minY + (data.maxSpawnBlockY - minY) / 2

          def findVertically(start: BlockPos): Option[BlockPos] = {
            if (world.isAirBlock(start) && world.isAirBlock(start.up))
              findDown(start)
            else
              findUp(start)
          }

          def findDown(start: BlockPos): Option[BlockPos] = {
            val pos = new MutableBlockPos(start)
            boundary:
              for (y <- (pos.getY to minY by -1) ++ (maxY to pos.getY by -1)) {
                pos.setY(y)
                if (!world.isAirBlock(pos)) {
                  break(
                    if (world.isSideSolid(pos, EnumFacing.UP))
                      Some(pos.up)
                    else
                      None
                  )
                }
              }
              None
          }

          def findUp(start: BlockPos): Option[BlockPos] = {
            val pos = new MutableBlockPos(start)
            boundary:
              for (y <- pos.getY to maxY) {
                pos.setY(y)
                if (world.isAirBlock(pos) && world.isAirBlock(pos.up))
                  break(
                    if (world.isSideSolid(pos.down, EnumFacing.UP))
                      Some(pos)
                    else
                      None
                  )
              }
              None
          }

          def findHorizontally(pos: BlockPos): Option[BlockPos] = {
            val coords = ((0 to 3) ++ (-3 to -1)).map(_ * 512)
            boundary:
              for {
                x <- coords
                z <- coords
                suitable <- findVertically(new BlockPos(x, pos.getY, z)) ++ findVertically(pos.add(x, 0, z))
              } {
                break(Some(suitable))
              }
              None
          }

          def ensurePlace(default: BlockPos, maybePos: Option[BlockPos]): BlockPos =
            maybePos match {
              case Some(pos) => pos
              case None =>
                buildPlatform(default)
                default
            }

          def buildPlatform(pos: BlockPos): Unit = {
            for (x <- -2 to 2)
              for (z <- -2 to 2) {
                world.setBlockState(pos.add(x, -1, z), Blocks.OBSIDIAN.getDefaultState)
                world.setBlockToAir(pos.add(x, 0, z))
                world.setBlockToAir(pos.add(x, 1, z))
                world.setBlockToAir(pos.add(x, 2, z))
              }
          }

          val start = new BlockPos(x, center, z)
          findVertically(start) match {
            case Some(pos) =>
              ReturnSolve.yes(pos)
            case None =>
              ReturnSolve.yes(ensurePlace(start, findHorizontally(start)))
          }
        case None => ReturnSolve.no()
      }
    } else
      ReturnSolve.no()
  }

  def isSafe(world: World, pos: BlockPos): Boolean =
    world.isAirBlock(pos) && world.isAirBlock(pos.up) && world.isSideSolid(pos, EnumFacing.UP)

  def getHorizontalPos(data: WorldData, realWorld: World): (Int, Int) = {
    val basePos = data.spawnLayer match {
      case layer: DimensionalLayer => layer.generator.proxyWorld.getSpawnPoint
      case _ => realWorld.getSpawnPoint
    }
    val fuzz: Int =
      math.min(
        realWorld match {
          case serverWorld: WorldServer =>
            realWorld.getWorldType.getSpawnFuzz(serverWorld, serverWorld.getMinecraftServer)
          case _ =>
            1
        },
        realWorld.getWorldBorder.getClosestDistance(basePos.getX(), basePos.getZ()).toInt
      )
    (basePos.getX + realWorld.rand.nextInt(fuzz / 2) - fuzz, basePos.getZ + realWorld.rand.nextInt(fuzz / 2) - fuzz)
  }

}
