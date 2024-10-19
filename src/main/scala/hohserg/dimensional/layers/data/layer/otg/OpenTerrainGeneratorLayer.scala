package hohserg.dimensional.layers.data.layer.otg

import com.pg85.otg.OTG
import com.pg85.otg.configuration.dimensions.DimensionConfig
import com.pg85.otg.forge.dimensions.{OTGDimensionManager, OTGWorldProvider}
import hohserg.dimensional.layers.data.layer.base.{DimensionalLayer, DimensionalLayerBounds}
import hohserg.dimensional.layers.preset.spec.OpenTerrainGeneratorLayerSpec
import hohserg.dimensional.layers.{CCWorld, CCWorldServer}
import net.minecraft.world.DimensionType
import net.minecraftforge.common.DimensionManager

import java.util

case class OpenTerrainGeneratorLayer(_realStartCubeY: Int, spec: OpenTerrainGeneratorLayerSpec, originalWorld: CCWorld) extends DimensionalLayer {

  override type Spec = OpenTerrainGeneratorLayerSpec
  override type G = OpenTerrainGeneratorGenerator

  lazy val presetConfig: DimensionConfig = spec.toOTGConfigServer

  override lazy val dimensionType: DimensionType = {
    val reservedIds: util.HashMap[Integer, String] = OTG.getEngine.getModPackConfigManager.getReservedDimIds
    val id = {
      for {
        i <- 3 to 2048
        presetReservingId = reservedIds.get(i)
        if (presetReservingId == null || presetReservingId == spec.presetName) && !DimensionManager.isDimensionRegistered(i)
      } yield i
    }.headOption.getOrElse(throw new IllegalStateException("there no free dimension id's"))

    presetConfig.DimensionId = id

    val result = DimensionType.register(presetConfig.PresetName, "OTG", id, classOf[OTGWorldProvider], false)
    OTGDimensionManager.registerDimension(id, result)
    result
  }

  override def isCubic: Boolean = false

  override val bounds: DimensionalLayerBounds = new DimensionalLayerBounds {
    override val realStartCubeY: Int = _realStartCubeY
    override val cubeHeight: Int = spec.height
    override val virtualStartCubeY: Int = 0
    override val virtualEndCubeY: Int = 16 - 1
  }

  override protected def createGenerator(original: CCWorldServer): OpenTerrainGeneratorGenerator = new OpenTerrainGeneratorGenerator(original, this)
}
