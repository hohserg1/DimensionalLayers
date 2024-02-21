package hohserg.dimensional.layers.worldgen.proxy

import hohserg.dimensional.layers.worldgen.BaseDimensionLayer
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer
import io.github.opencubicchunks.cubicchunks.api.util.CubePos
import io.github.opencubicchunks.cubicchunks.api.world.ICube
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{ClassInheritanceMultiMap, EnumFacing}
import net.minecraft.world.EnumSkyBlock
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.storage.ExtendedBlockStorage
import net.minecraftforge.common.capabilities.{Capability, CapabilityDispatcher}

import java.util

class ProxyCube(original: ICube, layer: BaseDimensionLayer) extends BaseProxyCube {

  override def proxyWorld(): ProxyWorldServer = layer.proxyWorld

  override def getBlockState(blockPos: BlockPos): IBlockState =
    original.getBlockState(layer.shift(blockPos))

  override def setBlockState(blockPos: BlockPos, iBlockState: IBlockState): IBlockState =
    original.setBlockState(layer.shift(blockPos), iBlockState)

  override def getBlockState(x: Int, y: Int, z: Int): IBlockState =
    if (layer.isInLayer(y))
      original.getBlockState(x, layer.shiftBlockY(y), z)
    else
      Blocks.AIR.getDefaultState

  override def getLightFor(enumSkyBlock: EnumSkyBlock, blockPos: BlockPos): Int =
    layer.executeInLayer(blockPos, original.getLightFor(enumSkyBlock, _), 0)

  override def setLightFor(enumSkyBlock: EnumSkyBlock, blockPos: BlockPos, i: Int): Unit =
    layer.executeInLayer(blockPos, original.setLightFor(enumSkyBlock, _, i), ())

  override def getTileEntity(pos: BlockPos, creationMode: Chunk.EnumCreateEntityType): TileEntity =
    original.getTileEntity(layer.shift(pos), creationMode)

  override def addTileEntity(tileEntity: TileEntity): Unit = {
    tileEntity.setPos(layer.shift(tileEntity.getPos))
    original.addTileEntity(tileEntity)
  }

  override def isEmpty: Boolean =
    original.isEmpty

  override def localAddressToBlockPos(i: Int): BlockPos =
    layer.markShifted(original.localAddressToBlockPos(i))

  override def getX: Int = original.getX

  override def getY: Int = ShiftedBlockPos.unshiftCubeY(original.getY, layer)

  override def getZ: Int = original.getZ

  override lazy val getCoords: CubePos = new CubePos(getX, getY, getZ)

  override def containsBlockPos(blockPos: BlockPos): Boolean = original.containsBlockPos(layer.shift(blockPos))

  override def getStorage: ExtendedBlockStorage = original.getStorage

  override def getTileEntityMap: util.Map[BlockPos, TileEntity] = original.getTileEntityMap //todo: maybe need to unshift it too?

  override def getEntitySet: ClassInheritanceMultiMap[Entity] = original.getEntitySet //todo: maybe need to unshift it too?

  override def addEntity(entity: Entity): Unit = {
    if (entity.world == layer.proxyWorld)
      layer.proxyWorld.spawnEntity(entity)
    else
      original.addEntity(entity)
  }

  override def removeEntity(entity: Entity): Boolean =
    original.removeEntity(entity)

  override def needsSaving(): Boolean = false

  override def isPopulated: Boolean = original.isPopulated

  override def isFullyPopulated: Boolean = original.isFullyPopulated

  override def isSurfaceTracked: Boolean = original.isSurfaceTracked

  override def isInitialLightingDone: Boolean = original.isInitialLightingDone

  override def isCubeLoaded: Boolean = original.isCubeLoaded

  override def hasLightUpdates: Boolean = original.hasLightUpdates

  override def getBiome(blockPos: BlockPos): Biome = original.getBiome(layer.shift(blockPos))

  override def setBiome(x: Int, z: Int, biome: Biome): Unit = original.setBiome(x, z, biome)

  override def getCapabilities: CapabilityDispatcher = original.getCapabilities

  override def getForceLoadStatus: util.EnumSet[ICube.ForcedLoadReason] = original.getForceLoadStatus

  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = original.hasCapability(capability, facing)

  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = original.getCapability(capability, facing)

  override def setBiome(x: Int, y: Int, z: Int, biome: Biome): Unit = original.setBiome(x, layer.shiftBlockY(y), z, biome)
}
