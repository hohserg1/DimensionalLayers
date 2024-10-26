package hohserg.dimensional.layers.feature

import hohserg.dimensional.layers.CCWorldServer
import hohserg.dimensional.layers.data.LayerManagerServer
import hohserg.dimensional.layers.data.layer.base.DimensionalLayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.util.math.{BlockPos, MathHelper}
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}

import scala.math.{abs, min}

@EventBusSubscriber
object ReplaceTeleportToDimension {

  @SubscribeEvent(priority = EventPriority.HIGH)
  def changeLayerInsteadOfDimension(e: EntityTravelToDimensionEvent): Unit = {
    val entity = e.getEntity
    entity.world match {
      case world: CCWorldServer =>
        LayerManagerServer.getWorldData(world).foreach { worldData =>
          val target = e.getDimension
          worldData.dimensionRelatedLayers.get(target).foreach { availableLayers =>
            e.setCanceled(true)
            val currentLayer = worldData.getLayerOf(entity)
            val nearestTargetLayer = availableLayers.minBy(l => min(
              abs(entity.posY - l.bounds.realStartBlockY),
              abs(entity.posY - l.bounds.realEndBlockY)
            ))

            val currentMovementFactor: Double = currentLayer.collect { case l: DimensionalLayer => movementFactorOfLayer(l) }.getOrElse(1)
            val targetMovementFactor: Double = movementFactorOfLayer(nearestTargetLayer)

            val moveFactor = currentMovementFactor / targetMovementFactor
            val newX = clampCoord(entity.posX * moveFactor, world).toInt >> 2 << 2
            val newZ = clampCoord(entity.posZ * moveFactor, world).toInt >> 2 << 2

            val midY = nearestTargetLayer.bounds.realStartBlockY + (nearestTargetLayer.bounds.realEndBlockY - nearestTargetLayer.bounds.realStartBlockY) / 2
            val fineY = nearestTargetLayer.generator.proxyWorld.getHeight(newX, newZ)
            val newY = if (isTopBlockValid(fineY, nearestTargetLayer)) fineY + nearestTargetLayer.bounds.realStartBlockY else midY

            val underNewPos = new BlockPos(newX, newY - 1, newZ)
            if (world.isAirBlock(underNewPos))
              world.setBlockState(underNewPos, Blocks.GLASS.getDefaultState)

            val pos1 = new BlockPos(newX, newY, newZ)
            val pos2 = new BlockPos(newX, newY + 1, newZ)
            if (!world.isAirBlock(pos1))
              world.setBlockToAir(pos1)
            if (!world.isAirBlock(pos2))
              world.setBlockToAir(pos2)


            def setLocation(x: Double, y: Double, z: Double): Unit =
              entity match {
                case player: EntityPlayerMP =>
                  val prevInvulnerableDimensionChange = player.invulnerableDimensionChange
                  player.invulnerableDimensionChange = true
                  player.connection.setPlayerLocation(x, y, z, 90, 0)
                  player.invulnerableDimensionChange = prevInvulnerableDimensionChange

                case _ =>
                  entity.setLocationAndAngles(x, y, z, 90, 0)
              }

            entity.timeUntilPortal = entity.getPortalCooldown
            setLocation(newX + 0.5, newY, newZ + 0.5)
          }
        }
      case _ =>
    }
  }

  private def isTopBlockValid(fineY: Int, nearestTargetLayer: DimensionalLayer): Boolean =
    fineY != 0 && fineY != nearestTargetLayer.bounds.virtualStartBlockY && fineY != nearestTargetLayer.bounds.virtualEndBlockY + 1


  def clampCoord(v: Double, world: CCWorldServer): Double =
    MathHelper.clamp(
      MathHelper.clamp(
        v,
        world.getWorldBorder.minZ + 16, world.getWorldBorder.maxZ - 16
      ),
      -29999872, 29999872
    )

  def movementFactorOfLayer(l: DimensionalLayer): Double = l.generator.proxyWorld.provider.getMovementFactor

}
