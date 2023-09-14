package hohserg.dimensional.layers.legacy.fake.world

import hohserg.dimensional.layers.LRUCache
import net.minecraft.crash.CrashReport
import net.minecraft.util.ReportedException
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.{Chunk, IChunkProvider}
import net.minecraft.world.gen.IChunkGenerator


class FakeChunkProvider(generator: IChunkGenerator, capacity: Int) extends IChunkProvider {
  case class ChunkEntry(chunk: Chunk, populatedSelf: Boolean, populatedLeft: Boolean, populatedLeftUp: Boolean, populatedUp: Boolean) {
    val isFullyPopulated: Boolean = populatedSelf && populatedLeft && populatedLeftUp && populatedUp
  }

  val chunkMap = new LRUCache[Long, ChunkEntry](capacity)

  override def getLoadedChunk(x: Int, z: Int): Chunk = chunkMap.get(ChunkPos.asLong(x, z)).chunk

  var needPopulate = true

  def populate(entry: ChunkEntry): ChunkEntry = {

    val x = entry.chunk.x
    val z = entry.chunk.z

    tryReported({
      generator.populate(x, z)
      generator.generateStructures(entry.chunk, x, z)

      chunkMap.computeIfPresent(ChunkPos.asLong(x + 1, z), (_: Long, e: ChunkEntry) => e.copy(populatedLeft = true))
      chunkMap.computeIfPresent(ChunkPos.asLong(x + 1, z + 1), (_: Long, e: ChunkEntry) => e.copy(populatedLeftUp = true))
      chunkMap.computeIfPresent(ChunkPos.asLong(x, z + 1), (_: Long, e: ChunkEntry) => e.copy(populatedLeftUp = true))

      chunkMap.putAndReturn(ChunkPos.asLong(x, z), entry.copy(populatedSelf = true))
    }, x, z)
  }

  def provideChunkWithPopulate(x: Int, z: Int): ChunkEntry = {
    val chunkKey = ChunkPos.asLong(x, z)

    if (needPopulate) {
      val currentEntry =
        if (chunkMap.containsKey(chunkKey))
          chunkMap.get(chunkKey)
        else
          createChunkEntry(x, z)

      if (currentEntry.isFullyPopulated)
        currentEntry
      else {
        needPopulate = false
        val left = provideChunkWithPopulate(x - 1, z)
        val leftUp = provideChunkWithPopulate(x - 1, z - 1)
        val up = provideChunkWithPopulate(x, z - 1)

        val rightUp = provideChunkWithPopulate(x + 1, z - 1)
        val right = provideChunkWithPopulate(x + 1, z)
        val rightDown = provideChunkWithPopulate(x + 1, z + 1)
        val down = provideChunkWithPopulate(x, z + 1)
        val leftDown = provideChunkWithPopulate(x - 1, z + 1)

        if (!currentEntry.populatedLeft)
          populate(left)

        if (!currentEntry.populatedLeftUp)
          populate(leftUp)

        if (!currentEntry.populatedUp)
          populate(up)

        if (!currentEntry.populatedSelf)
          populate(currentEntry)

        needPopulate = true

        chunkMap.putAndReturn(chunkKey, ChunkEntry(currentEntry.chunk, populatedSelf = true, populatedLeft = true, populatedLeftUp = true, populatedUp = true))
      }

    } else {
      if (chunkMap.containsKey(chunkKey))
        chunkMap.get(chunkKey)
      else {
        chunkMap.putAndReturn(chunkKey, createChunkEntry(x, z))
      }
    }
  }

  private def createChunkEntry(x: Int, z: Int): ChunkEntry =
    tryReported(
      ChunkEntry(generator.generateChunk(x, z), populatedSelf = false, populatedLeft = false, populatedLeftUp = false, populatedUp = false),
      x, z)


  def tryReported[A](v: => A, x: Int, z: Int): A = {
    try {
      v
    } catch {
      case throwable: Throwable =>
        val crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk")
        val crashreportcategory = crashreport.makeCategory("Chunk to be generated")
        crashreportcategory.addCrashSection("Location", x + ", " + z)
        crashreportcategory.addCrashSection("Position hash", ChunkPos.asLong(x, z))
        crashreportcategory.addCrashSection("Generator", generator)
        throw new ReportedException(crashreport)
    }
  }

  override def provideChunk(x: Int, z: Int): Chunk = {
    provideChunkWithPopulate(x, z).chunk
  }

  override def tick(): Boolean = false

  override def makeString(): String = ""

  override def isChunkGeneratedAt(x: Int, z: Int): Boolean = chunkMap.containsKey(ChunkPos.asLong(x, z))
}
