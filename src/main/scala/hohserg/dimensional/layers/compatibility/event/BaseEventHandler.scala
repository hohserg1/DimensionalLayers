package hohserg.dimensional.layers.compatibility.event

import hohserg.dimensional.layers.compatibility.event.mixin.{AccessorASMEventHandler, AccessorEventBus}
import hohserg.dimensional.layers.data.LayerManager
import hohserg.dimensional.layers.data.layer.base.{DimensionalLayer, DimensionalLayerBounds, Layer}
import hohserg.dimensional.layers.worldgen.proxy.ShiftedBlockPos
import hohserg.dimensional.layers.worldgen.proxy.client.ProxyWorldClient
import hohserg.dimensional.layers.{CCWorld, CCWorldClient, CCWorldServer}
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

  def handleEntityBasedEvent[SidedProxyWorld <: CCWorld](entity: Entity, proxyEvent: => E,
                                                         getProxyWorld: Layer => SidedProxyWorld): Unit = {
    if (entity != null) {
      val originalWorld = entity.world.asInstanceOf[CCWorldServer]
      handleHeightBasedEvent[SidedProxyWorld](
        Coords.blockToCube((entity.posY + 0.5D).toInt), originalWorld, proxyEvent,
        setup =
          (dimensional, proxyWorld) => {
            entity.world = proxyWorld
            entity.posY = ShiftedBlockPos.unshiftBlockY(entity.posY, dimensional.bounds)
          },
        clear =
          (dimensional, proxyWorld) => {
            entity.world = originalWorld
            entity.posY = ShiftedBlockPos.shiftBlockY(entity.posY, dimensional.bounds)
          },
        getProxyWorld
      )
    }
  }

  def handleHeightBasedEventClient(cubeY: Int, originalWorld: CCWorldClient, proxyEvent: => E,
                                   setup: (DimensionalLayer, ProxyWorldClient) => Unit,
                                   clear: (DimensionalLayer, ProxyWorldClient) => Unit): Unit =
    handleHeightBasedEvent[ProxyWorldClient](cubeY, originalWorld, proxyEvent, setup, clear, _.clientProxyWorld)

  def handleHeightBasedEvent[SidedProxyWorld](cubeY: Int, originalWorld: CCWorld, proxyEvent: => E,
                                              setup: (DimensionalLayer, SidedProxyWorld) => Unit,
                                              clear: (DimensionalLayer, SidedProxyWorld) => Unit,
                                              getProxyWorld: Layer => SidedProxyWorld): Unit = {
    LayerManager.getWorldData(originalWorld) match {
      case Some(worldData) =>
        worldData.layerAtCubeY.get(cubeY) match {
          case Some(dimensional: DimensionalLayer) if dimensional.bounds.isInstanceOf[DimensionalLayerBounds] =>

            setup(dimensional, getProxyWorld(dimensional))

            post(proxyEvent)

            clear(dimensional, getProxyWorld(dimensional))

          case _ =>
        }
      case None =>
    }
  }
}
