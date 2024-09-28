package hohserg.dimensional.layers.worldgen.proxy.server

import hohserg.dimensional.layers.CCWorldServer
import hohserg.dimensional.layers.data.layer.base.DimensionalLayer
import hohserg.dimensional.layers.worldgen.proxy.ProxyChunkProviderCommon
import io.github.opencubicchunks.cubicchunks.api.world.ICubeProviderServer.Requirement._
import io.github.opencubicchunks.cubicchunks.api.world.{ICube, ICubeProviderServer}
import net.minecraft.entity.EnumCreatureType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.ChunkProviderServer
import net.minecraft.world.{World, WorldServer}

import java.util

case class ProxyChunkProviderServer(proxy: ProxyWorldServer, original: CCWorldServer, layer: DimensionalLayer)
  extends ChunkProviderServer(proxy.asInstanceOf[WorldServer], proxy.getSaveHandler.getChunkLoader(proxy.provider), null)
    with ICubeProviderServer
    with ProxyChunkProviderCommon {

  override def loadChunk(x: Int, z: Int): Chunk = ???

  override def loadChunk(x: Int, z: Int, runnable: Runnable): Chunk = ???

  override def saveChunks(all: Boolean): Boolean = false

  override def flushToDisk(): Unit = ()

  override def canSave: Boolean = false

  override def getPossibleCreatures(creatureType: EnumCreatureType, pos: BlockPos): util.List[Biome.SpawnListEntry] =
    layer.generator.getPossibleCreatures(creatureType, pos)

  override def getNearestStructurePos(worldIn: World, structureName: String, position: BlockPos, findUnexplored: Boolean): BlockPos =
    null

  override def isInsideStructure(worldIn: World, structureName: String, pos: BlockPos): Boolean =
    false

  //cubic

  override def getColumn(cx: Int, cz: Int, requirement: ICubeProviderServer.Requirement): Chunk =
    requirement match {
      case GET_CACHED =>
        getLoadedColumn(cx, cz)
      case LOAD | GENERATE | POPULATE | LIGHT =>
        provideColumn(cx, cz)
    }

  override def getCube(cx: Int, cy: Int, cz: Int, requirement: ICubeProviderServer.Requirement): ICube =
    requirement match {
      case GET_CACHED =>
        getLoadedCube(cx, cy, cz)
      case LOAD | GENERATE | POPULATE | LIGHT =>
        getCube(cx, cy, cz)
    }

  override def getCubeNow(cx: Int, cy: Int, cz: Int, requirement: ICubeProviderServer.Requirement): ICube =
    getCube(cx, cy, cz, requirement)

  override def isCubeGenerated(cx: Int, cy: Int, cz: Int): Boolean =
    getLoadedCube(cx, cy, cz) != null || original.getCubeCache.isCubeGenerated(cx, cy, cz)
}
