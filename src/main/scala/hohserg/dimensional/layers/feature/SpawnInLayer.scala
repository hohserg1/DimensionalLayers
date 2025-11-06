package hohserg.dimensional.layers.feature

import gloomyfolken.hooklib.api.*
import hohserg.dimensional.layers.*
import hohserg.dimensional.layers.data.LayerManager
import io.github.opencubicchunks.cubicchunks.core.world.SpawnPlaceFinder
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
          val (x, z) = getHorizontalPos(world)
          println("bruh", data.preset)
          println(data.spawnLayer)
          val center = data.minSpawnBlockY + (data.maxSpawnBlockY - data.minSpawnBlockY) / 2
          val pos = new BlockPos(x, center, z)
          ReturnSolve.yes(
            if (world.isAirBlock(pos) && world.isAirBlock(pos.up))
              findDown(world, pos, data.minSpawnBlockY)
            else
              findUp(world, pos, data.maxSpawnBlockY)
          )
        case None => ReturnSolve.no()
      }
    } else
      ReturnSolve.no()
  }

  def findDown(world: World, start: BlockPos, min: Int): BlockPos = {
    val pos = new MutableBlockPos(start)
    boundary:
      for (y <- pos.getY to min by -1) {
        pos.setY(y)
        if (!world.isAirBlock(pos))
          break(pos.up)
      }
      start
  }

  def findUp(world: World, start: BlockPos, max: Int): BlockPos = {
    val pos = new MutableBlockPos(start)
    boundary:
      for (y <- pos.getY to max) {
        pos.setY(y)
        if (world.isAirBlock(pos) && world.isAirBlock(pos.up))
          break(pos)
      }
      start
  }

  def getHorizontalPos(world: World): (Int, Int) = {
    val basePos = world.getSpawnPoint
    val fuzz: Int =
      math.min(
        world match {
          case serverWorld: WorldServer =>
            world.getWorldType.getSpawnFuzz(serverWorld, serverWorld.getMinecraftServer)
          case _ =>
            1
        },
        world.getWorldBorder.getClosestDistance(basePos.getX(), basePos.getZ()).toInt
      )
    (basePos.getX + world.rand.nextInt(fuzz / 2) - fuzz, basePos.getZ + world.rand.nextInt(fuzz / 2) - fuzz)
  }

}
