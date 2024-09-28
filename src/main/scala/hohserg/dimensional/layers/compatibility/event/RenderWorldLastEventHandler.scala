package hohserg.dimensional.layers.compatibility.event

import hohserg.dimensional.layers.CCWorldClient
import hohserg.dimensional.layers.data.LayerManagerClient
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class RenderWorldLastEventHandler(modidSet: Set[String])
  extends BaseEventHandler[RenderWorldLastEvent](modidSet) {

  override def isClient = true

  @SubscribeEvent
  override def handle(e: RenderWorldLastEvent): Unit = {
    super.handle(e)

    handleEntityBasedEvent(LayerManagerClient, Minecraft.getMinecraft.getRenderViewEntity, Minecraft.getMinecraft.world.asInstanceOf[CCWorldClient], e, _.clientProxyWorld)
  }
}
