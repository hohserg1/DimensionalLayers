package hohserg.dimensional.layers.worldgen.proxy

import hohserg.dimensional.layers.CCWorld
import hohserg.dimensional.layers.data.layer.base.DimensionalLayerBounds
import io.github.opencubicchunks.cubicchunks.api.util.CubePos
import io.github.opencubicchunks.cubicchunks.api.world.ICube
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{ClassInheritanceMultiMap, EnumFacing}
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.storage.ExtendedBlockStorage
import net.minecraft.world.{EnumSkyBlock, World}
import net.minecraftforge.common.capabilities.{Capability, CapabilityDispatcher}

import java.util
import scala.annotation.nowarn

class ProxyCube(original: ICube, layerBounds: DimensionalLayerBounds, _proxyWorld: CCWorld & ProxyWorldCommon) extends BaseProxyCube {

  override def proxyWorld(): World = _proxyWorld

  override def getBlockState(blockPos: BlockPos): IBlockState = {
    original.getBlockState(layerBounds.shift(blockPos))
  }

  override def setBlockState(blockPos: BlockPos, iBlockState: IBlockState): IBlockState = {
    original.setBlockState(layerBounds.shift(blockPos), iBlockState)
  }

  override def getBlockState(x: Int, y: Int, z: Int): IBlockState = {
    if (layerBounds.isInLayer(y))
      original.getBlockState(x, layerBounds.shiftBlockY(y), z)
    else
      Blocks.AIR.getDefaultState
  }

  override def getLightFor(enumSkyBlock: EnumSkyBlock, blockPos: BlockPos): Int = {
    layerBounds.executeInLayer(blockPos, original.getLightFor(enumSkyBlock, _), 0)
  }

  override def setLightFor(enumSkyBlock: EnumSkyBlock, blockPos: BlockPos, i: Int): Unit = {
    layerBounds.executeInLayer(blockPos, original.setLightFor(enumSkyBlock, _, i), ())
  }

  override def getTileEntity(pos: BlockPos, creationMode: Chunk.EnumCreateEntityType): TileEntity = {
    original.getTileEntity(layerBounds.shift(pos), creationMode)
  }

  override def addTileEntity(tileEntity: TileEntity): Unit = {
    tileEntity.setPos(layerBounds.shift(tileEntity.getPos))
    original.addTileEntity(tileEntity)
  }

  override def isEmpty: Boolean = {
    original.isEmpty
  }

  override def localAddressToBlockPos(i: Int): BlockPos = {
    layerBounds.markShifted(original.localAddressToBlockPos(i))
  }

  override def getX: Int = original.getX

  override def getY: Int = ShiftedBlockPos.unshiftCubeY(original.getY, layerBounds)

  override def getZ: Int = original.getZ

  override lazy val getCoords: CubePos = new CubePos(getX, getY, getZ)

  override def containsBlockPos(blockPos: BlockPos): Boolean = original.containsBlockPos(layerBounds.shift(blockPos))

  override def getStorage: ExtendedBlockStorage = original.getStorage

  override def getTileEntityMap: util.Map[BlockPos, TileEntity] = original.getTileEntityMap //todo: maybe need to unshift it too?

  override def getEntitySet: ClassInheritanceMultiMap[Entity] = original.getEntitySet //todo: maybe need to unshift it too?

  override def addEntity(entity: Entity): Unit = {
    if (entity.world == proxyWorld)
      proxyWorld.spawnEntity(entity)
    else
      original.addEntity(entity)
  }

  override def removeEntity(entity: Entity): Boolean = {
    original.removeEntity(entity)
  }

  override def needsSaving(): Boolean = false

  override def isPopulated: Boolean = original.isPopulated

  override def isFullyPopulated: Boolean = original.isFullyPopulated

  override def isSurfaceTracked: Boolean = original.isSurfaceTracked

  override def isInitialLightingDone: Boolean = original.isInitialLightingDone

  override def isCubeLoaded: Boolean = original.isCubeLoaded

  override def hasLightUpdates: Boolean = original.hasLightUpdates

  override def getBiome(blockPos: BlockPos): Biome = original.getBiome(layerBounds.shift(blockPos))

  @nowarn("msg=deprecated")
  override def setBiome(x: Int, z: Int, biome: Biome): Unit = {
    original.setBiome(x, z, biome)
  }

  override def getCapabilities: CapabilityDispatcher = original.getCapabilities

  override def getForceLoadStatus: util.EnumSet[ICube.ForcedLoadReason] = original.getForceLoadStatus

  override def hasCapability(capability: Capability[?], facing: EnumFacing): Boolean = original.hasCapability(capability, facing)

  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = original.getCapability(capability, facing)

  override def setBiome(x: Int, y: Int, z: Int, biome: Biome): Unit = original.setBiome(x, layerBounds.shiftBlockY(y), z, biome)
}
