package hohserg.dimensional.layers.compatibility.geographicraft

import hohserg.dimensional.layers.CCWorldServer
import hohserg.dimensional.layers.data.LayerManagerServer
import hohserg.dimensional.layers.data.layer.base.Layer
import net.minecraft.world.World

import scala.collection.JavaConverters._

object ScalaHelper {
  def getWorldData(world: World): java.util.List[Layer] = {
    LayerManagerServer.getWorldData(world.asInstanceOf[CCWorldServer]) match {
      case Some(x) => x.layers.map(_._2).asJava
      case None => null
    }
  }
}
