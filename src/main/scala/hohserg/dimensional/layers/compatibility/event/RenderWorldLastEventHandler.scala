package hohserg.dimensional.layers.compatibility.event

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

    handleEntityBasedEvent(Minecraft.getMinecraft.getRenderViewEntity, e)
  }
}
