package hohserg.dimensional.layers.worldgen.proxy.server

import hohserg.dimensional.layers.worldgen.proxy.FakeCubicWorldCommon
import io.github.opencubicchunks.cubicchunks.api.util.CubePos
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldServer
import net.minecraftforge.common.ForgeChunkManager

trait FakeCubicWorldServer extends FakeCubicWorldCommon with ICubicWorldServer {

  override def unloadOldCubes(): Unit = ()

  override def forceChunk(ticket: ForgeChunkManager.Ticket, cubePos: CubePos): Unit = ()

  override def reorderChunk(ticket: ForgeChunkManager.Ticket, cubePos: CubePos): Unit = ()

  override def unforceChunk(ticket: ForgeChunkManager.Ticket, cubePos: CubePos): Unit = ()

}
