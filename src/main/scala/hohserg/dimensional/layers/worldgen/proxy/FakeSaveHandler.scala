package hohserg.dimensional.layers.worldgen.proxy

import hohserg.dimensional.layers.Main
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.datafix.DataFixesManager
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.storage.IChunkLoader
import net.minecraft.world.gen.structure.template.TemplateManager
import net.minecraft.world.storage.{IPlayerFileData, ISaveHandler, WorldInfo}
import net.minecraft.world.{World, WorldProvider}

import java.io.File

class FakeSaveHandler(val loadWorldInfo: WorldInfo) extends ISaveHandler {

  override def checkSessionLock(): Unit = ()

  override def getChunkLoader(provider: WorldProvider): IChunkLoader = new IChunkLoader {
    override def loadChunk(worldIn: World, x: Int, z: Int): Chunk = ???

    override def saveChunk(worldIn: World, chunkIn: Chunk): Unit = ???

    override def saveExtraChunkData(worldIn: World, chunkIn: Chunk): Unit = ???

    override def chunkTick(): Unit = ???

    override def flush(): Unit = ???

    override def isChunkGeneratedAt(x: Int, z: Int): Boolean = ???
  }

  override def saveWorldInfoWithPlayer(worldInformation: WorldInfo, tagCompound: NBTTagCompound): Unit = ???

  override def saveWorldInfo(worldInformation: WorldInfo): Unit = ???

  override def getPlayerNBTManager: IPlayerFileData = ???

  override def flush(): Unit = ???

  override lazy val getWorldDirectory: File = {
    val r = new File("./" + Main.modid + "/fake_save_handle/")
    r.mkdirs()
    r
  }

  override def getMapFileFromName(mapName: String): File = ???

  val templateManager: TemplateManager = new TemplateManager(new File(new File(".", "world"), "structures").toString, DataFixesManager.createFixer())

  override def getStructureTemplateManager: TemplateManager = templateManager
}
