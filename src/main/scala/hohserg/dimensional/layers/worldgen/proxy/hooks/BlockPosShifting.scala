package hohserg.dimensional.layers.worldgen.proxy.hooks

import gloomyfolken.hooklib.api.{Hook, HookContainer, OnBegin, ReturnSolve}
import hohserg.dimensional.layers.worldgen.proxy.ShiftedBlockPos
import net.minecraft.util.math.{BlockPos, Vec3i}

@HookContainer
object BlockPosShifting {

  @Hook
  @OnBegin
  def add(self: BlockPos, vec: Vec3i): ReturnSolve[BlockPos] =
    vec match {
      case shifted: ShiftedBlockPos => ReturnSolve.yes(shifted.add(self))
      case _ => ReturnSolve.no()
    }

  @Hook
  @OnBegin
  def subtract(self: BlockPos, vec: Vec3i): ReturnSolve[BlockPos] =
    vec match {
      case shifted: ShiftedBlockPos => ReturnSolve.yes(shifted.subtracted(self))
      case _ => ReturnSolve.no()
    }

}
