package hohserg.dimensional.layers.worldgen.proxy

import hohserg.dimensional.layers.Main
import hohserg.dimensional.layers.worldgen.BaseDimensionLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{BlockPos, Vec3i}

class ShiftedBlockPos(private val x: Int,
                      private val y: Int,
                      private val z: Int,
                      private val layer: BaseDimensionLayer)
  extends BlockPos(x, ShiftedBlockPos.shiftBlockY(y, layer), z) {

  def unshift: BlockPos = new BlockPos(x, y, z)

  def isInLayer: Boolean = layer.virtualStartBlockY <= y && y <= layer.virtualEndBlockY

  def clamp: ShiftedBlockPos =
    if (y < layer.virtualStartBlockY)
      new ShiftedBlockPos(x, layer.virtualStartBlockY, z, layer)
    else if (layer.virtualEndBlockY < y)
      new ShiftedBlockPos(x, layer.virtualEndBlockY, z, layer)
    else
      this

  override def offset(facing: EnumFacing, n: Int): BlockPos =
    if (n == 0)
      this
    else
      new ShiftedBlockPos(x + facing.getXOffset * n, y + facing.getYOffset * n, z + facing.getZOffset * n, layer)

  override def add(dx: Double, dy: Double, dz: Double): BlockPos =
    if (dx == 0.0D && dy == 0.0D && dz == 0.0D)
      this
    else
      new ShiftedBlockPos(x + dx.toInt, y + dy.toInt, z + dz.toInt, layer)

  override def add(dx: Int, dy: Int, dz: Int): BlockPos =
    if (dx == 0 && dy == 0 && dz == 0)
      this
    else
      new ShiftedBlockPos(x + dx, y + dy, z + dz, layer)

  override def add(vec: Vec3i): BlockPos =
    vec match {
      case shifted: ShiftedBlockPos =>
        checkPosInSameLayer(shifted, "sum")

        new ShiftedBlockPos(x + shifted.x, y + shifted.y, z + shifted.z, layer)

      case _ =>
        super.add(vec)
    }

  override def subtract(vec: Vec3i): BlockPos = {
    vec match {
      case shifted: ShiftedBlockPos =>
        checkPosInSameLayer(shifted, "subtract")

        new ShiftedBlockPos(x - shifted.x, y - shifted.y, z - shifted.z, layer)

      case _ =>
        super.subtract(vec)
    }
  }

  private def checkPosInSameLayer(shifted: ShiftedBlockPos, operationName: String): Unit = {
    if (shifted.layer != layer)
      Main.proxy.printError(
        "wtf, attempt to " + operationName + " different layers shifted poses: " + this + ", " + shifted,
        new IllegalArgumentException("shifted pos from another layer")
      )
  }

  def subtracted(from: Vec3i): BlockPos =
    from match {
      case shifted: ShiftedBlockPos => shifted.subtract(this)
      case _ => new ShiftedBlockPos(from.getX - x, from.getY - y, from.getZ - z, layer)
    }


}

object ShiftedBlockPos {
  def apply(pos: BlockPos, layer: BaseDimensionLayer): ShiftedBlockPos =
    pos match {
      case already: ShiftedBlockPos => already
      case _ => new ShiftedBlockPos(pos.getX, pos.getY, pos.getZ, layer)
    }

  def markShifted(pos: BlockPos, layer: BaseDimensionLayer): ShiftedBlockPos =
    pos match {
      case already: ShiftedBlockPos => already
      case _ => new ShiftedBlockPos(pos.getX, unshiftBlockY(pos.getY, layer), pos.getZ, layer)
    }

  def unshift(pos: BlockPos): BlockPos =
    pos match {
      case shifted: ShiftedBlockPos => shifted.unshift
      case already => already
    }

  def shiftBlockY[N](y: N, layer: BaseDimensionLayer)(implicit n: Numeric[N]): N = {
    import n._
    y - fromInt(layer.virtualStartBlockY) + fromInt(layer.realStartBlockY)
  }

  def unshiftBlockY[N](y: N, layer: BaseDimensionLayer)(implicit n: Numeric[N]): N = {
    import n._
    y + fromInt(layer.virtualStartBlockY) - fromInt(layer.realStartBlockY)
  }

  def unshiftCubeY(y: Int, layer: BaseDimensionLayer): Int = {
    y + layer.virtualStartCubeY - layer.realStartCubeY
  }
}
