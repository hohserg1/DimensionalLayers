package hohserg.dimensional.layers.compatibility.event

import hohserg.dimensional.layers.data.{LayerManagerClient, LayerManagerServer}
import hohserg.dimensional.layers.{CCWorldClient, CCWorldServer}
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class LivingUpdateEventHandler(modidSet: Set[String], entityFilter: EntityLivingBase => Boolean)
  extends BaseEventHandler[LivingEvent.LivingUpdateEvent](modidSet) {

  @SubscribeEvent
  override def handle(e: LivingEvent.LivingUpdateEvent): Unit = {
    super.handle(e)

    val entity = e.getEntityLiving
    if (entity.isEntityAlive)
      if (entityFilter(entity)) {
        entity.world match {
          case w: CCWorldClient =>
            handleEntityBasedEvent(LayerManagerClient, entity, w, new LivingEvent.LivingUpdateEvent(entity), _.clientProxyWorld)
          case w: CCWorldServer =>
            handleEntityBasedEvent(LayerManagerServer, entity, w, new LivingEvent.LivingUpdateEvent(entity), _.generator.proxyWorld)
        }
      }
  }
}
