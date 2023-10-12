package hohserg.dimensional.layers.worldgen.proxy

import hohserg.dimensional.layers.worldgen.VanillaLayer
import net.minecraft.util.math.BlockPos

class ShiftedBlockPos(x: Int, y: Int, z: Int, layer: VanillaLayer) extends BlockPos(x, y + layer.startBlockY, z) {
  def isInLayer: Boolean = layer.virtualStartBlockY <= y && y <= layer.virtualEndBlockY
}

object ShiftedBlockPos {
  def apply(pos: BlockPos, layer: VanillaLayer): ShiftedBlockPos =
    pos match {
      case already: ShiftedBlockPos => already
      case _ => new ShiftedBlockPos(pos.getX, pos.getY, pos.getZ, layer)
    }
}
