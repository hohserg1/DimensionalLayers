package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.CCWorld
import net.minecraft.world.World

trait LayerManager {

  def haveWorldLayers(world: World): Boolean

  def getWorldData(world: World): Option[WorldData]

}

object LayerManager {
  def getSided(world: World): LayerManager = {
    if (world.isRemote)
      LayerManagerClient
    else
      LayerManagerServer
  }
}
