package hohserg.dimensional.layers.preset

import hohserg.dimensional.layers.DimensionalLayersWorldType
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import net.minecraft.world.WorldType

import scala.annotation.nowarn

object CubicWorldTypeHelper {
  @nowarn("msg=with")
  lazy val possibleWorldTypes =
    WorldType.WORLD_TYPES
             .toIndexedSeq
             .filter(_ != null)
             .filter(_.canBeCreated)
             .collect { case cubic: WorldType with ICubicWorldType => cubic }
             .filter(_ != DimensionalLayersWorldType)
}
