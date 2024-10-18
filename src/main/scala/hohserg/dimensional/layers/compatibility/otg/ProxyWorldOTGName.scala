package hohserg.dimensional.layers.compatibility.otg

import com.pg85.otg.forge.world.WorldHelper
import gloomyfolken.hooklib.api.{Hook, HookContainer, OnBegin, ReturnSolve}
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer
import net.minecraft.world.World

@HookContainer
object ProxyWorldOTGName {

  @Hook(targetMethod = "getName")
  @OnBegin
  def addSpecialCaseForProxyWorld(worldHelper: WorldHelper, world: World): ReturnSolve[String] =
    if (world.isInstanceOf[ProxyWorldServer])
      ReturnSolve.yes(world.provider.getDimensionType.getName)
    else
      ReturnSolve.no()

}
