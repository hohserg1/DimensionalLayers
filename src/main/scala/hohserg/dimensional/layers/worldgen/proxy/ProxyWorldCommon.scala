package hohserg.dimensional.layers.worldgen.proxy

import net.minecraft.world.World

trait ProxyWorldCommon {
  this: World =>

  def initWorld(): Unit = {
    provider.setWorld(this)
    provider.setDimension(layer.dimensionType.getId)

  }

}
