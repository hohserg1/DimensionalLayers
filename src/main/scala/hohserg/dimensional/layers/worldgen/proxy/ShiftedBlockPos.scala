package hohserg.dimensional.layers.worldgen.proxy

import hohserg.dimensional.layers.worldgen.VanillaLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{BlockPos, Vec3i}

class ShiftedBlockPos(private val x: Int, private val y: Int, private val z: Int, private val layer: VanillaLayer) extends BlockPos(x, y + layer.startBlockY, z) {
  def isInLayer: Boolean = layer.virtualStartBlockY <= y && y <= layer.virtualEndBlockY

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
        if (shifted.layer != layer)
          println("wtf, attempt to sum different layers shifted poses: ", this, shifted)

        new ShiftedBlockPos(x + shifted.x, y + shifted.y, z + shifted.z, layer)

      case _ =>
        super.add(vec)
    }

  override def subtract(vec: Vec3i): BlockPos = {
    vec match {
      case shifted: ShiftedBlockPos =>
        if (shifted.layer != layer)
          println("wtf, attempt to subtract different layers shifted poses: ", this, shifted)

        new ShiftedBlockPos(x - shifted.x, y - shifted.y, z - shifted.z, layer)

      case _ =>
        super.subtract(vec)
    }
  }

  def subtracted(from: Vec3i): BlockPos =
    from match {
      case shifted: ShiftedBlockPos => shifted.subtract(this)
      case _ => new ShiftedBlockPos(from.getX - x, from.getY - y, from.getZ - z, layer)
    }


}

object ShiftedBlockPos {
  def apply(pos: BlockPos, layer: VanillaLayer): ShiftedBlockPos =
    pos match {
      case already: ShiftedBlockPos => already
      case _ => new ShiftedBlockPos(pos.getX, pos.getY, pos.getZ, layer)
    }

  def markShifted(pos: BlockPos, layer: VanillaLayer): ShiftedBlockPos =
    pos match {
      case already: ShiftedBlockPos => already
      case _ => new ShiftedBlockPos(pos.getX, pos.getY - layer.startBlockY, pos.getZ, layer)
    }
}
