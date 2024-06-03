package hohserg.dimensional.layers.compatibility.event

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class RenderTickEventHandler(modidSet: Set[String])
  extends BaseEventHandler[TickEvent.RenderTickEvent](modidSet) {

  override def isClient = true

  @SubscribeEvent
  override def handle(e: TickEvent.RenderTickEvent): Unit = {
    super.handle(e)

    handleEntityBasedEvent(Minecraft.getMinecraft.getRenderViewEntity, e, _.clientProxyWorld)
  }
}