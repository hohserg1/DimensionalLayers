package hohserg.dimensional.layers.compatibility.event

import hohserg.dimensional.layers.data.{LayerManagerClient, LayerManagerServer}
import hohserg.dimensional.layers.{CCWorldClient, CCWorldServer}
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

class LivingUpdateEventHandler(modidSet: Set[String], entityFilter: EntityLivingBase => Boolean)
  extends BaseEventHandler[LivingEvent.LivingUpdateEvent](modidSet) {

  @SubscribeEvent
  override def handle(e: LivingEvent.LivingUpdateEvent): Unit = {
    super.handle(e)

    val entity: EntityLivingBase = e.getEntityLiving
    if (entity.isEntityAlive)
      if (entityFilter(entity)) {
        if (entity.world.isRemote)
          handleClient(entity)
        else
          handleEntityBasedEvent(LayerManagerServer, entity, entity.world.asInstanceOf[CCWorldServer], new LivingEvent.LivingUpdateEvent(entity), _.generator.proxyWorld)
      }
  }

  @SideOnly(Side.CLIENT)
  def handleClient(entity: EntityLivingBase): Unit =
    handleEntityBasedEvent(LayerManagerClient, entity, entity.world.asInstanceOf[CCWorldClient], new LivingEvent.LivingUpdateEvent(entity), _.clientProxyWorld)
}
