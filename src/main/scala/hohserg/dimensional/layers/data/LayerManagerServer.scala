package hohserg.dimensional.layers.data

import com.pg85.otg.OTG
import com.pg85.otg.configuration.dimensions.{DimensionConfig, DimensionsConfig}
import hohserg.dimensional.layers.data.layer.otg.OpenTerrainGeneratorLayer
import hohserg.dimensional.layers.{CCWorldServer, Main}
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

import java.io.File
import java.util
import scala.collection.JavaConverters._

@EventBusSubscriber
object LayerManagerServer extends LayerManager[CCWorldServer] {

  override protected def createWorldData(world: CCWorldServer): WorldData = {
    val r = super.createWorldData(world)
    val otgLayers = r.layers
      .collect { case (_, l: OpenTerrainGeneratorLayer) => l }
    if (otgLayers.nonEmpty) {
      val firstLayer = otgLayers.head
      val fakeWorldFolder = new File(world.getSaveHandler.getWorldDirectory, Main.modid + "/fake_save_handler/otg/")
      fakeWorldFolder.mkdirs()
      val proxyWorldName = fakeWorldFolder.getName
      val forgeWorldConfig = new DimensionsConfig(fakeWorldFolder.getParentFile, proxyWorldName)
      forgeWorldConfig.ModPackConfigName = null
      forgeWorldConfig.ModPackConfigVersion = 0
      forgeWorldConfig.WorldName = proxyWorldName
      forgeWorldConfig.Overworld = firstLayer.presetConfig
      forgeWorldConfig.Dimensions = new util.ArrayList[DimensionConfig]
      forgeWorldConfig.Dimensions.addAll(otgLayers.map(_.presetConfig).asJava)

      OTG.setDimensionsConfig(forgeWorldConfig)
    }
    r
  }
}
