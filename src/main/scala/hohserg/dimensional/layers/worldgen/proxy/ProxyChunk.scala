package hohserg.dimensional.layers.worldgen.proxy

import hohserg.dimensional.layers.CCWorld
import hohserg.dimensional.layers.data.layer.base.DimensionalLayerBounds
import io.github.opencubicchunks.cubicchunks.api.util.Coords
import io.github.opencubicchunks.cubicchunks.api.world.IColumn
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.storage.ExtendedBlockStorage
import net.minecraft.world.{EnumSkyBlock, World}

class ProxyChunk(proxy: CCWorld with ProxyWorldCommon, original: Chunk, layerBounds: DimensionalLayerBounds) extends Chunk(original.getWorld, original.x, original.z) {
  val column = original.asInstanceOf[IColumn]

  override def getBlockState(pos: BlockPos): IBlockState =
    pos match {
      case pos: ShiftedBlockPos =>
        if (pos.isInLayer)
          getBlockStateShifted(pos.getX, pos.getY, pos.getZ)
        else
          Blocks.AIR.getDefaultState
      case _ =>
        getBlockState(pos.getX, pos.getY, pos.getZ);
    }

  override def getBlockState(x: Int, y: Int, z: Int): IBlockState =
    if (layerBounds.isInLayer(y))
      getBlockStateShifted(x, layerBounds.shiftBlockY(y), z)
    else
      Blocks.AIR.getDefaultState

  def getBlockStateShifted(x: Int, y: Int, z: Int): IBlockState =
    original.getBlockState(x, y, z)

  override def setBlockState(pos: BlockPos, state: IBlockState): IBlockState =
    executeInLayer(pos, original.setBlockState(_, state), null)

  override def getHeightValue(x: Int, z: Int): Int = proxy.asInstanceOf[World].getHeight(x, z)

  override def getTopFilledSegment: Int = {
    for (i <- layerBounds.realEndCubeY to layerBounds.realStartCubeY by -1) {
      val cube = original.asInstanceOf[IColumn].getCube(i)
      if (!cube.isEmpty) {
        return Coords.cubeToMinBlock(i + layerBounds.virtualStartCubeY)
      }
    }
    0
  }

  override def getLightFor(`type`: EnumSkyBlock, pos: BlockPos): Int =
    executeInLayer(pos, original.getLightFor(`type`, _), 0)

  override def setLightFor(`type`: EnumSkyBlock, pos: BlockPos, value: Int): Unit =
    executeInLayer(pos, original.setLightFor(`type`, _, value), ())

  override def getLightSubtracted(pos: BlockPos, amount: Int): Int =
    executeInLayer(pos, original.getLightSubtracted(_, amount), 0)

  override def addEntity(entityIn: Entity): Unit = {
    entityIn.posY = layerBounds.shiftBlockY(entityIn.posY)
    entityIn.prevPosY = entityIn.posY
    original.addEntity(entityIn)
  }

  override def removeEntity(entityIn: Entity): Unit =
    original.removeEntity(entityIn)

  override def removeEntityAtIndex(entityIn: Entity, index: Int): Unit =
    original.removeEntityAtIndex(entityIn, index)

  override def canSeeSky(pos: BlockPos): Boolean = original.canSeeSky(layerBounds.shift(pos))

  override def getTileEntity(pos: BlockPos, creationMode: Chunk.EnumCreateEntityType): TileEntity =
    original.getTileEntity(layerBounds.shift(pos), creationMode)

  override def addTileEntity(pos: BlockPos, tileEntityIn: TileEntity): Unit = {
    tileEntityIn.setPos(layerBounds.shift(tileEntityIn.getPos))
    original.addTileEntity(tileEntityIn.getPos, tileEntityIn)
  }

  override def removeTileEntity(pos: BlockPos): Unit =
    original.removeTileEntity(layerBounds.shift(pos))

  override def getPrecipitationHeight(pos: BlockPos): BlockPos =
    proxy.getHeight(pos)

  override def isEmptyBetween(startY: Int, endY: Int): Boolean =
    original.isEmptyBetween(layerBounds.shiftBlockY(startY), layerBounds.shiftBlockY(endY))

  override lazy val getBlockStorageArray: Array[ExtendedBlockStorage] =
    (for {
      i <- 0 to 15
    } yield column.getCube(layerBounds.realStartCubeY + i).getStorage).toArray


  private def executeInLayer[A](pos: BlockPos, f: ShiftedBlockPos => A, default: A): A =
    layerBounds.executeInLayer(pos, f, default)
}
