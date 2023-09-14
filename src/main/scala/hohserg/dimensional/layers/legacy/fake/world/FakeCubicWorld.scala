package hohserg.dimensional.layers.legacy.fake.world

import io.github.opencubicchunks.cubicchunks.api.util._
import io.github.opencubicchunks.cubicchunks.api.world.{IColumn, ICube}
import io.github.opencubicchunks.cubicchunks.api.worldgen.ICubeGenerator
import io.github.opencubicchunks.cubicchunks.core.asm.mixin.ICubicWorldInternal
import io.github.opencubicchunks.cubicchunks.core.lighting.LightingManager
import io.github.opencubicchunks.cubicchunks.core.server.{CubeProviderServer, SpawnCubes, VanillaNetworkHandler}
import io.github.opencubicchunks.cubicchunks.core.util.world.{CubeSplitTickList, CubeSplitTickSet}
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.ForgeChunkManager

import java.util.function.Predicate

trait FakeCubicWorld extends ICubicWorldInternal.Server {
  override def isCubicWorld: Boolean = false

  override def getVanillaNetworkHandler: VanillaNetworkHandler = ???

  override def initCubicWorldServer(intRange: IntRange, intRange1: IntRange): Unit = ()

  override def getCubeCache: CubeProviderServer = throw new NotCubicChunksWorldException()

  //override def getFirstLightProcessor: FirstLightProcessor = throw new NotCubicChunksWorldException()

  override def removeForcedCube(iCube: ICube): Unit = ()

  override def addForcedCube(iCube: ICube): Unit = ()

  override def getForcedCubes: XYZMap[ICube] = null

  override def getForcedColumns: XZMap[IColumn] = null

  override def getScheduledTicks: CubeSplitTickSet = null

  override def getThisTickScheduledTicks: CubeSplitTickList = null

  override def getSpawnArea: SpawnCubes = null

  override def setSpawnArea(spawnCubes: SpawnCubes): Unit = ()

  override def doCompatibilityGeneration(): ICubicWorldInternal.CompatGenerationScope = null

  override def isCompatGenerationScope: Boolean = false

  override def getCubeGenerator: ICubeGenerator = null

  override def unloadOldCubes(): Unit = ()

  override def forceChunk(ticket: ForgeChunkManager.Ticket, cubePos: CubePos): Unit = ()

  override def reorderChunk(ticket: ForgeChunkManager.Ticket, cubePos: CubePos): Unit = ()

  override def unforceChunk(ticket: ForgeChunkManager.Ticket, cubePos: CubePos): Unit = ()

  override def tickCubicWorld(): Unit = ()

  override def getLightingManager: LightingManager = null

  override def getCubeFromCubeCoords(i: Int, i1: Int, i2: Int): Cube = null

  override def getCubeFromBlockCoords(blockPos: BlockPos): Cube = null

  override def fakeWorldHeight(i: Int): Unit = ()

  override def testForCubes(cubePos: CubePos, cubePos1: CubePos, predicate: Predicate[_ >: ICube]): Boolean = false

  override def getEffectiveHeight(i: Int, i1: Int): Int = 0

  override def isBlockColumnLoaded(blockPos: BlockPos): Boolean = false

  override def isBlockColumnLoaded(blockPos: BlockPos, b: Boolean): Boolean = false

  override def getMinGenerationHeight: Int = 0

  override def getMaxGenerationHeight: Int = 256
}
