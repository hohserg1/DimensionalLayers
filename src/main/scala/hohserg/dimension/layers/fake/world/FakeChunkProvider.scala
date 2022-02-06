package hohserg.dimension.layers.fake.world

import hohserg.dimension.layers.LRUCache
import net.minecraft.crash.CrashReport
import net.minecraft.util.ReportedException
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.{Chunk, IChunkProvider}

class FakeChunkProvider(world: FakeWorld) extends IChunkProvider {


  val generator = world.chunkGeneratorFactory(world)

  val chunkMap = new LRUCache[Long, Chunk](world.capacity)

  override def getLoadedChunk(x: Int, z: Int): Chunk = chunkMap.get(ChunkPos.asLong(x, z))

  def populate(x: Int, z: Int): Unit =
    provideChunk(x, z).populate(this, generator)


  override def provideChunk(x: Int, z: Int): Chunk = {
    val i = ChunkPos.asLong(x, z)
    if (chunkMap.containsKey(i))
      chunkMap.get(i)
    else {
      try {
        val chunk = generator.generateChunk(x, z)
        chunkMap.put(i, chunk)
        chunk
      }
      catch {
        case throwable: Throwable =>
          val crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk")
          val crashreportcategory = crashreport.makeCategory("Chunk to be generated")
          crashreportcategory.addCrashSection("Location", x + ", " + z)
          crashreportcategory.addCrashSection("Position hash", i)
          crashreportcategory.addCrashSection("Generator", generator)
          throw new ReportedException(crashreport)
      }
    }
  }

  override def tick(): Boolean = false

  override def makeString(): String = ""

  override def isChunkGeneratedAt(x: Int, z: Int): Boolean = chunkMap.containsKey(ChunkPos.asLong(x, z))
}
