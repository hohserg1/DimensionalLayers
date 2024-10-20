package hohserg.dimensional.layers.preset.spec

import com.pg85.otg.OTG
import com.pg85.otg.configuration.dimensions.{DimensionConfig, DimensionConfigBase, DimensionConfigGui}
import com.pg85.otg.configuration.world.WorldConfig
import hohserg.dimensional.layers.Main
import hohserg.dimensional.layers.Main.otgModid
import hohserg.dimensional.layers.data.layer.otg.OpenTerrainGeneratorLayer
import hohserg.dimensional.layers.gui.preset.list.{GuiLayerEntry, GuiLayersList, GuiOpenTerrainGeneratorLayerEntry}
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import java.io.File

case class OpenTerrainGeneratorLayerSpec(presetName: String, seedOverride: Option[Long] = None, configYml: Option[String] = None) extends LayerSpec {
  if (!Main.otgPresent)
    throw new IllegalStateException("Attempt to use OTG layer with no OTG in modpack. Install OTG and try again")

  override def height: Int = 16

  override val toLayer = OpenTerrainGeneratorLayer

  @SideOnly(Side.CLIENT)
  override def toGuiLayerEntry(parent: GuiLayersList): GuiLayerEntry = new GuiOpenTerrainGeneratorLayerEntry(parent, this)

  @Optional.Method(modid = otgModid)
  def toOTGConfig[A <: DimensionConfigBase](fromYamlString: String => A, defaultFactory: (String, Int, Boolean, WorldConfig) => A): A = {
    val r = configYml
      .map(fromYamlString)
      .getOrElse(defaultFactory(
        presetName,
        0, false,
        WorldConfig.fromDisk(new File(OTG.getEngine.getOTGRootFolder.getAbsolutePath + "/worlds/" + presetName))
      ))
    seedOverride.map(_.toString).foreach(r.Seed = _)
    r
  }

  @Optional.Method(modid = otgModid)
  def toOTGConfigServer: DimensionConfig = toOTGConfig(
    DimensionConfig.fromYamlString,
    new DimensionConfig(_, _, _, _)
  )

  @Optional.Method(modid = otgModid)
  def toOTGConfigClient: DimensionConfigGui = toOTGConfig(
    DimensionConfigGui.fromYamlString,
    new DimensionConfigGui(_, _, _, _)
  )
}