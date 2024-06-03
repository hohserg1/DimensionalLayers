package hohserg.dimensional.layers.data.layer.base

import hohserg.dimensional.layers.worldgen.proxy.ShiftedBlockPos
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import net.minecraft.util.math.BlockPos

trait LayerBounds {
  def realStartCubeY: Int

  def cubeHeight: Int

  def realEndCubeY: Int = realStartCubeY + cubeHeight - 1

  def realStartBlockY: Int = Coords.cubeToMinBlock(realStartCubeY)

  def realEndBlockY: Int = Coords.cubeToMaxBlock(realEndCubeY)
}

trait DimensionalLayerBounds extends LayerBounds {
  def virtualStartCubeY: Int

  def virtualEndCubeY: Int

  def virtualStartBlockY: Int = Coords.cubeToMinBlock(virtualStartCubeY)

  def virtualEndBlockY: Int = Coords.cubeToMaxBlock(virtualEndCubeY)

  def shift(pos: BlockPos): ShiftedBlockPos = ShiftedBlockPos(pos, this)

  def shiftBlockY[N: Numeric](y: N): N = ShiftedBlockPos.shiftBlockY(y, this)

  def markShifted(pos: BlockPos): ShiftedBlockPos = ShiftedBlockPos.markShifted(pos, this)

  def isInLayer(y: Int): Boolean =
    virtualStartBlockY <= y && y <= virtualEndBlockY

  def executeInLayer[A](pos: BlockPos, f: ShiftedBlockPos => A, default: A): A = {
    val p = shift(pos)
    if (p.isInLayer)
      f(p)
    else
      default
  }
}