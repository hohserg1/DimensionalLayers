package hohserg.dimensional.layers.feature.overworld.portal

import net.minecraft.block.BlockPortal.AXIS
import net.minecraft.block.state.IBlockState
import net.minecraft.block.{Block, BlockDirt, BlockTallGrass}
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.event.entity.player.BonemealEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.{Event, SubscribeEvent}

import scala.jdk.CollectionConverters.*

@EventBusSubscriber
object PortalIgnition {

  def isFineBottom(block: Block): Boolean = block == Blocks.STONE

  def isFineTop(block: Block): Boolean = block == Blocks.DIRT || block == Blocks.GRASS

  def isFineMid(block: Block): Boolean = isFineTop(block) || isFineBottom(block)


  @SubscribeEvent
  def onUseBoneMeal(e: BonemealEvent): Unit = {
    if (e.getWorld.isRemote)
      return

    if (!isFineTop(e.getBlock.getBlock))
      return

    if (!e.getWorld.isAirBlock(e.getPos.down()))
      return

    val internalHeight = (1 to 23).takeWhile(i => e.getWorld.isAirBlock(e.getPos.down(i))).last

    if (internalHeight < 3)
      return

    if (!isFineBottom(e.getWorld.getBlockState(e.getPos.down(internalHeight + 1)).getBlock))
      return

    if (tryX(e.getWorld, e.getPos, internalHeight, e))
      return

    tryZ(e.getWorld, e.getPos, internalHeight, e)
  }

  def isAreaEmpty(world: World, from: BlockPos, to: BlockPos): Boolean = {
    isAreaFine(world, from, to, _ == Blocks.AIR)
  }

  def isAreaFine(world: World, from: BlockPos, to: BlockPos, isSuitable: Block => Boolean): Boolean = {
    BlockPos.getAllInBoxMutable(from, to).asScala.forall(p => isSuitable(world.getBlockState(p).getBlock))
  }

  def tryAxis(positive: EnumFacing, negative: EnumFacing, world: World, clickedPos: BlockPos, internalHeight: Int, e: BonemealEvent): Boolean = {
    val east = (0 to 23).takeWhile(i => world.isAirBlock(clickedPos.down().offset(positive, i))).last

    val eastTop = clickedPos.down().offset(positive, east + 1)
    if (isFineTop(world.getBlockState(eastTop).getBlock) &&
      isFineBottom(world.getBlockState(clickedPos.down(internalHeight).offset(positive, east + 1)).getBlock)) {

      val west = (0 to 23 - east).takeWhile(i => world.isAirBlock(clickedPos.down().offset(negative, i))).last

      val westTop = clickedPos.down().offset(negative, west + 1)
      if (isFineTop(world.getBlockState(westTop).getBlock) &&
        isFineBottom(world.getBlockState(clickedPos.down(internalHeight).offset(negative, west + 1)).getBlock)) {

        val from = clickedPos.down().offset(positive, east)
        val to = clickedPos.down(internalHeight).offset(negative, west)

        if (!isAreaEmpty(world, from, to))
          return false

        if (!isAreaFine(world, clickedPos.offset(positive, east), clickedPos.offset(negative, west), isFineTop))
          return false

        if (!isAreaFine(world, clickedPos.offset(positive, east).down(internalHeight + 1), clickedPos.offset(negative, west).down(internalHeight + 1), isFineBottom))
          return false

        if (!isAreaFine(world, clickedPos.offset(positive, east + 1).down(2), clickedPos.offset(positive, east + 1).down(internalHeight - 1), isFineMid))
          return false

        if (!isAreaFine(world, clickedPos.offset(negative, west + 1).down(2), clickedPos.offset(negative, west + 1).down(internalHeight - 1), isFineMid))
          return false

        val portal = BlockOverworldPortal.getDefaultState.withProperty(AXIS, positive.getAxis)
        BlockPos.getAllInBoxMutable(from, to).asScala.foreach(p => world.setBlockState(p, portal, 2))

        def randomTallGrass(): IBlockState = {
          Blocks.TALLGRASS.getDefaultState.withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.values()(world.rand.nextInt(BlockTallGrass.EnumType.values().length)))
        }

        def growGrass(p: BlockPos): Unit = {
          val fromState = world.getBlockState(p)
          if (fromState.getBlock == Blocks.DIRT && fromState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT) {
            world.setBlockState(p, Blocks.GRASS.getDefaultState, 2)
          }
          if (world.getBlockState(p).getBlock == Blocks.GRASS) {
            val over = p.up()
            if (world.isAirBlock(over)) {
              if (world.rand.nextInt(8) == 0)
                world.getBiome(over).plantFlower(world, world.rand, over)
              else
                world.setBlockState(over, randomTallGrass(), 2)
            }
          }
        }

        BlockPos.getAllInBoxMutable(clickedPos.offset(positive, east), clickedPos.offset(negative, west)).asScala.foreach(growGrass)
        growGrass(eastTop)
        growGrass(westTop)
        e.setResult(Event.Result.ALLOW)

        return true
      }
    }
    false
  }

  def tryX(world: World, clickedPos: BlockPos, internalHeight: Int, e: BonemealEvent): Boolean = {
    tryAxis(EnumFacing.EAST, EnumFacing.WEST, world, clickedPos, internalHeight, e)
  }

  def tryZ(world: World, clickedPos: BlockPos, internalHeight: Int, e: BonemealEvent): Boolean = {
    tryAxis(EnumFacing.SOUTH, EnumFacing.NORTH, world, clickedPos, internalHeight, e)
  }
}
