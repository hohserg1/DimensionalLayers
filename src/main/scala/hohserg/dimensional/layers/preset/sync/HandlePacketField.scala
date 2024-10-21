package hohserg.dimensional.layers.preset.sync

import gloomyfolken.hooklib.api.{Hook, OnReturn}
import net.minecraft.client.Minecraft
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.network.play.server.SPacketJoinGame
import net.minecraft.world.WorldSettings
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
//@HookContainer
object HandlePacketField {

  @SideOnly(Side.CLIENT)
  @Hook
  @OnReturn
  def onReceivePacket(netHandler: NetHandlerPlayClient, packet: SPacketJoinGame): Unit = {
    val worldInfo = Minecraft.getMinecraft.world.getWorldInfo
    val worldSettings = new WorldSettings(worldInfo)
    worldSettings.setGeneratorOptions(AddFieldToPacket.generatorOptions.get(packet))
    worldInfo.populateFromWorldSettings(worldSettings)
  }
}
