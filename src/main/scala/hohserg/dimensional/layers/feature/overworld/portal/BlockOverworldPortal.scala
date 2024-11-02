package hohserg.dimensional.layers.feature.overworld.portal

import net.minecraft.block.BlockPortal.AXIS
import net.minecraft.block.state.IBlockState
import net.minecraft.block.state.pattern.BlockPattern
import net.minecraft.block.{Block, BlockPortal, SoundType}
import net.minecraft.entity.passive.EntityChicken
import net.minecraft.entity.{Entity, EntityList}
import net.minecraft.init.Blocks
import net.minecraft.item.ItemMonsterPlacer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

import java.util.Random

object BlockOverworldPortal extends BlockPortal {
  setRegistryName("overworld_portal")
  setHardness(-1)
  setSoundType(SoundType.GLASS)
  setLightLevel(0.75F)

  override def updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random): Unit = {
    if (!worldIn.provider.isSurfaceWorld && worldIn.getGameRules.getBoolean("doMobSpawning"))
      return

    if (worldIn.getBlockState(pos.down()).getBlock != Blocks.STONE)
      return

    if (rand.nextInt(2000) > worldIn.getDifficulty.getId)
      return

    val entity = ItemMonsterPlacer.spawnCreature(worldIn, EntityList.getKey(classOf[EntityChicken]), pos.getX + 0.5, pos.getY, pos.getZ + 0.5)
    entity.timeUntilPortal = entity.getPortalCooldown
  }

  override def onEntityCollision(worldIn: World, pos: BlockPos, state: IBlockState, entityIn: Entity): Unit = {
    if (entityIn.getRidingEntity != null || entityIn.getPassengers.size() > 0)
      return

    if (worldIn.isRemote) {
      entityIn.inPortal = true
      return
    }

    if (entityIn.timeUntilPortal > 0) {
      entityIn.timeUntilPortal = entityIn.getPortalCooldown
    } else {
      entityIn.portalCounter += 2
      if (entityIn.portalCounter > entityIn.getMaxInPortalTime * 3) {
        entityIn.timeUntilPortal = entityIn.getPortalCooldown
        entityIn.portalCounter = 0
        entityIn.changeDimension(0)
      }
    }
  }

  def isValidNeighbor(block: Block): Boolean =
    block == this || block == Blocks.STONE || block == Blocks.DIRT || block == Blocks.GRASS

  override def neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos): Unit = {
    val facing = state.getValue(AXIS) match {
      case EnumFacing.Axis.X =>
        EnumFacing.EAST
      case EnumFacing.Axis.Z =>
        EnumFacing.NORTH
    }

    if (!isValidNeighbor(worldIn.getBlockState(pos.down()).getBlock) ||
      !isValidNeighbor(worldIn.getBlockState(pos.up()).getBlock) ||
      !isValidNeighbor(worldIn.getBlockState(pos.offset(facing)).getBlock) ||
      !isValidNeighbor(worldIn.getBlockState(pos.offset(facing.getOpposite)).getBlock)
    ) {
      worldIn.setBlockToAir(pos)
    }
  }

  override def trySpawnPortal(worldIn: World, pos: BlockPos): Boolean = false

  override def createPatternHelper(worldIn: World, p_181089_2_ : BlockPos): BlockPattern.PatternHelper = ???

}
