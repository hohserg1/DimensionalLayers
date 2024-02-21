package hohserg.dimensional.layers.compatibility.event

import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class LivingUpdateEventHandler(modidSet: Set[String], entityFilter: EntityLivingBase => Boolean)
  extends BaseEventHandler[LivingEvent.LivingUpdateEvent](modidSet) {

  @SubscribeEvent
  override def handle(e: LivingEvent.LivingUpdateEvent): Unit = {
    super.handle(e)

    val entity = e.getEntityLiving
    if (entityFilter(entity)) {
      handleEntityBasedEvent(entity, new LivingEvent.LivingUpdateEvent(entity))
    }
  }
}
