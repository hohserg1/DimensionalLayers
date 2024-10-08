package hohserg.dimensional.layers.worldgen.proxy

import io.github.opencubicchunks.cubicchunks.api.util.CubePos
import io.github.opencubicchunks.cubicchunks.api.world.{ICube, ICubicWorld}
import net.minecraft.util.math.BlockPos

import java.util.function.Predicate

trait FakeCubicWorldCommon extends ICubicWorld {

  override def testForCubes(cubePos: CubePos, cubePos1: CubePos, predicate: Predicate[_ >: ICube]): Boolean = false

  override def getEffectiveHeight(i: Int, i1: Int): Int = 0

  override def isBlockColumnLoaded(blockPos: BlockPos): Boolean = false

  override def isBlockColumnLoaded(blockPos: BlockPos, b: Boolean): Boolean = false

  override def getMinGenerationHeight: Int = 0

  override def getMaxGenerationHeight: Int = 256

}
