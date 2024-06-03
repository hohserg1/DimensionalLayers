package hohserg.dimensional.layers.compatibility.event

import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.EntityViewRenderEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class FogDensityHandler(modidSet: Set[String])
  extends BaseEventHandler[EntityViewRenderEvent.FogDensity](modidSet) {

  override def isClient = true

  @SubscribeEvent
  override def handle(e: EntityViewRenderEvent.FogDensity): Unit = {
    super.handle(e)

    handleEntityBasedEvent(Minecraft.getMinecraft.getRenderViewEntity, e, _.clientProxyWorld)
  }
}
