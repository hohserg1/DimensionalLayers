package hohserg.dimensional.layers.compatibility.event

import hohserg.dimensional.layers.compatibility.event.mixin.{AccessorASMEventHandler, AccessorEventBus}
import hohserg.dimensional.layers.worldgen.BaseDimensionLayer
import hohserg.dimensional.layers.worldgen.proxy.ShiftedBlockPos
import hohserg.dimensional.layers.{CCWorld, DimensionalLayersManager}
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import net.minecraft.entity.Entity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.{ASMEventHandler, Event}


class BaseEventHandler[E <: Event](modidSet: Set[String]) {
  private var listeners: Array[ASMEventHandler] = _

  def isClient = false

  private def initListeners(e: E): Unit = {
    if (listeners == null) {
      listeners = e.getListenerList.getListeners(MinecraftForge.EVENT_BUS.asInstanceOf[AccessorEventBus].getBusID)
        .collect { case asm: ASMEventHandler if modidSet.contains(asm.asInstanceOf[AccessorASMEventHandler].getOwner.getModId) => asm }
    }
  }


  def handle(e: E): Unit = {
    initListeners(e)
  }

  def post(fakeEvent: E): Unit = {
    listeners.foreach(l => l.invoke(fakeEvent))
  }

  def handleEntityBasedEvent(entity: Entity, proxyEvent: => E): Unit = {
    if (entity != null) {
      val originalWorld = entity.world.asInstanceOf[CCWorld]
      handleHeightBasedEvent(
        Coords.blockToCube((entity.posY + 0.5D).toInt), originalWorld, proxyEvent,
        setup =
          dimensional => {
            dimensional.proxyWorld.isRemote = isClient
            entity.world = dimensional.proxyWorld
            entity.posY = ShiftedBlockPos.unshiftBlockY(entity.posY, dimensional)
          },
        clear =
          dimensional => {
            dimensional.proxyWorld.isRemote = false
            entity.world = originalWorld
            entity.posY = ShiftedBlockPos.shiftBlockY(entity.posY, dimensional)
          }
      )
    }
  }

  def handleHeightBasedEvent(cubeY: Int, originalWorld: CCWorld, proxyEvent: => E, setup: BaseDimensionLayer => Unit, clear: BaseDimensionLayer => Unit): Unit = {
    DimensionalLayersManager.getWorldData(originalWorld) match {
      case Some(worldData) =>
        worldData.layerAtCubeY.get(cubeY) match {
          case Some(dimensional: BaseDimensionLayer) =>

            setup(dimensional)

            post(proxyEvent)

            clear(dimensional)

          case _ =>
        }
      case None =>
    }
  }
}
