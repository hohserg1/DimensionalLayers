package hohserg.dimensional.layers.compatibility.bop

import biomesoplenty.common.world.generator.tree._
import gloomyfolken.hooklib.api.{Hook, OnExpression, Shift}
import hohserg.dimensional.layers.CCWorldServer
import hohserg.dimensional.layers.data.LayerManagerServer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

import java.util.Random

//@HookContainer
object ReplaceHardcodedHeight {

  def _256(): Int = 256;

  def _255(): Int = 255;

  def worldMaxY(world: World): Int =
    LayerManagerServer.getWorldData(world.asInstanceOf[CCWorldServer]) match {
      case Some(x) => x.maxBlockY
      case None => 255
    }

  @Hook(targetMethod = "generate")
  @OnExpression(expressionPattern = "_256", shift = Shift.INSTEAD)
  def replaceMaxHeightBasicTree(self: GeneratorBasicTree, world: World, random: Random, pos: BlockPos): Int = worldMaxY(world)

  @Hook(targetMethod = "checkSpace")
  @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
  def replaceMaxHeightBayouTree(self: GeneratorBayouTree, world: World, pos: BlockPos, rootHeight: Int, middleHeight: Int, height: Int): Int = worldMaxY(world)

  @Hook(targetMethod = "checkSpace")
  @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
  def replaceMaxHeightBulbTree(self: GeneratorBulbTree, world: World, pos: BlockPos, rootHeight: Int, middleHeight: Int, height: Int): Int = worldMaxY(world)

  @Hook(targetMethod = "checkEnoughSpace")
  @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
  def replaceMaxHeightHugeTree(self: GeneratorHugeTree, world: World, pos: BlockPos, height: Int): Int = worldMaxY(world)

  @Hook(targetMethod = "checkSpace")
  @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
  def replaceMaxHeightMangroveTree(self: GeneratorMangroveTree, world: World, pos: BlockPos, rootHeight: Int, middleHeight: Int, height: Int): Int = worldMaxY(world)

  @Hook(targetMethod = "checkSpace")
  @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
  def replaceMaxHeightPalmTree(self: GeneratorPalmTree, world: World, pos: BlockPos, height: Int, radius: Int): Int = worldMaxY(world)

  @Hook(targetMethod = "checkSpace")
  @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
  def replaceMaxHeightPineTree(self: GeneratorPineTree, world: World, pos: BlockPos, height: Int, radius: Int): Int = worldMaxY(world)

  @Hook(targetMethod = "checkSpace")
  @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
  def replaceMaxHeightProfileTree(self: GeneratorProfileTree, world: World, pos: BlockPos, height: Int, radius: Int): Int = worldMaxY(world)

  @Hook(targetMethod = "checkSpace")
  @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
  def replaceMaxHeightRedwoodTree(self: GeneratorRedwoodTree, world: World, pos: BlockPos, height: Int, radius: Int): Int = worldMaxY(world)

  @Hook(targetMethod = "canPlaceHere")
  @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
  def replaceMaxHeightRedwoodTreeThin(self: GeneratorRedwoodTreeThin, world: World, pos: BlockPos, height: Int, radius: Int): Int = worldMaxY(world)

  @Hook(targetMethod = "generate")
  @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
  def replaceMaxHeightRedwoodTreeThin(self: GeneratorRedwoodTreeThin, world: World, random: Random, pos: BlockPos): Int = worldMaxY(world)

  @Hook(targetMethod = "checkSpace")
  @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
  def replaceMaxHeightTaigaTree(self: GeneratorTaigaTree, world: World, pos: BlockPos, baseHeight: Int, height: Int): Int = worldMaxY(world)
}
