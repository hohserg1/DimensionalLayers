package hohserg.dimensional.layers.asm.dev.tweaks

import gloomyfolken.hooklib.api.{Hook, HookContainer, OnMethodCall, Shift}
import io.netty.channel.ChannelHandler
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side

@HookContainer
object FixBukkitCrash {

  @Hook(targetMethod = "newChannel")
  @OnMethodCall(value = "values", shift = Shift.INSTEAD)
  def filterSides(networkRegistry: NetworkRegistry, name: String, handlers: Array[ChannelHandler]): Array[Side] =
    Array(Side.CLIENT, Side.SERVER)

  @Hook(targetMethod = "newChannel")
  @OnMethodCall(value = "values", shift = Shift.INSTEAD)
  def filterSides(networkRegistry: NetworkRegistry, container: ModContainer, name: String, handlers: Array[ChannelHandler]): Array[Side] =
    Array(Side.CLIENT, Side.SERVER)
}
