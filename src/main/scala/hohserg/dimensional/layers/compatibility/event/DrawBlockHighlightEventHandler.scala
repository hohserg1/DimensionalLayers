package hohserg.dimensional.layers.compatibility.event

import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class DrawBlockHighlightEventHandler(modidSet: Set[String])
  extends BaseEventHandler[DrawBlockHighlightEvent](modidSet) {

  override def isClient = true

  @SubscribeEvent
  override def handle(e: DrawBlockHighlightEvent): Unit = {
    super.handle(e)

    handleEntityBasedEvent(Minecraft.getMinecraft.getRenderViewEntity, e, _.clientProxyWorld)
  }
}
