package hohserg.dimensional.layers.feature.overworld.portal

import net.minecraft.block.Block
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@EventBusSubscriber(Array(Side.CLIENT))
object Registration {

  @SubscribeEvent
  def registerBlocks(e: RegistryEvent.Register[Block]): Unit = {
    e.getRegistry.register(BlockOverworldPortal)
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  def registerPortalTexture(e: TextureStitchEvent.Pre): Unit = {
    e.getMap.setTextureEntry(new PortalTextureAtlasSprite)
  }

}
