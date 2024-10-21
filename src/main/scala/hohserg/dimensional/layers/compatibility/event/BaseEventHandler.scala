package hohserg.dimensional.layers.compatibility.event

import hohserg.dimensional.layers.data.layer.base.{DimensionalLayer, DimensionalLayerBounds}
import hohserg.dimensional.layers.data.{LayerManager, LayerManagerClient}
import hohserg.dimensional.layers.worldgen.proxy.ShiftedBlockPos
import hohserg.dimensional.layers.worldgen.proxy.client.ProxyWorldClient
import hohserg.dimensional.layers.{CCWorld, CCWorldClient}
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import net.minecraft.entity.Entity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.{ASMEventHandler, Event}

import scala.util.Try


class BaseEventHandler[E <: Event](modidSet: Set[String]) {
  private var listeners: Array[ASMEventHandler] = _

  def isClient = false

  private def initListeners(e: E): Unit = {
    if (listeners == null) {
      listeners = e.getListenerList.getListeners(AccessorEvents.busID.get(MinecraftForge.EVENT_BUS))
        .collect { case asm: ASMEventHandler if modidSet.contains(AccessorEvents.owner.get(asm).getModId) => asm }
    }
  }


  def handle(e: E): Unit = {
    initListeners(e)
    if (listeners.isEmpty)
      MinecraftForge.EVENT_BUS.unregister(this)
  }

  def post(fakeEvent: E): Unit = {
    listeners.foreach(l => l.invoke(fakeEvent))
  }

  def handleEntityBasedEvent[SidedProxyWorld <: CCWorld, SidedOriginalWorld <: CCWorld](layerManager: LayerManager[SidedOriginalWorld],
                                                                                        entity: Entity,
                                                                                        originalWorld: SidedOriginalWorld,
                                                                                        proxyEvent: => E,
                                                                                        getProxyWorld: DimensionalLayer => SidedProxyWorld): Unit = {
    if (entity != null) {
      handleHeightBasedEvent[SidedProxyWorld, SidedOriginalWorld](
        layerManager,
        Coords.blockToCube((entity.posY + 0.5D).toInt),
        originalWorld,
        proxyEvent,
        setup =
          (dimensional, proxyWorld) => {
            entity.world = proxyWorld
            entity.posY = ShiftedBlockPos.unshiftBlockY(entity.posY, dimensional.bounds)
            entity.lastTickPosY = ShiftedBlockPos.unshiftBlockY(entity.lastTickPosY, dimensional.bounds)
            entity.prevPosY = ShiftedBlockPos.unshiftBlockY(entity.prevPosY, dimensional.bounds)
          },
        clear =
          (dimensional, proxyWorld) => {
            entity.world = originalWorld
            entity.posY = ShiftedBlockPos.shiftBlockY(entity.posY, dimensional.bounds)
            entity.lastTickPosY = ShiftedBlockPos.shiftBlockY(entity.lastTickPosY, dimensional.bounds)
            entity.prevPosY = ShiftedBlockPos.shiftBlockY(entity.prevPosY, dimensional.bounds)
          },
        getProxyWorld
      )
    }
  }

  def handleHeightBasedEventClient(cubeY: Int, originalWorld: CCWorldClient, proxyEvent: => E,
                                   setup: (DimensionalLayer, ProxyWorldClient) => Unit,
                                   clear: (DimensionalLayer, ProxyWorldClient) => Unit): Unit =
    handleHeightBasedEvent[ProxyWorldClient, CCWorldClient](LayerManagerClient, cubeY, originalWorld, proxyEvent, setup, clear, _.clientProxyWorld)

  def handleHeightBasedEvent[SidedProxyWorld <: CCWorld, SidedOriginalWorld <: CCWorld](layerManager: LayerManager[SidedOriginalWorld],
                                                                                        cubeY: Int, originalWorld: SidedOriginalWorld,
                                                                                        proxyEvent: => E,
                                                                                        setup: (DimensionalLayer, SidedProxyWorld) => Unit,
                                                                                        clear: (DimensionalLayer, SidedProxyWorld) => Unit,
                                                                                        getProxyWorld: DimensionalLayer => SidedProxyWorld): Unit = {
    layerManager.getWorldData(originalWorld) match {
      case Some(worldData) =>
        worldData.layerAtCubeY.get(cubeY) match {
          case Some(dimensional: DimensionalLayer) if dimensional.bounds.isInstanceOf[DimensionalLayerBounds] =>

            setup(dimensional, getProxyWorld(dimensional))

            Try(post(proxyEvent))

            clear(dimensional, getProxyWorld(dimensional))

          case _ =>
        }
      case None =>
    }
  }
}
