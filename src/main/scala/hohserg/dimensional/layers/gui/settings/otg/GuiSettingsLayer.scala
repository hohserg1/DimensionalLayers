package hohserg.dimensional.layers.gui.settings.otg

import com.pg85.otg.configuration.dimensions.DimensionConfig
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.RelativeCoord
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer
import hohserg.dimensional.layers.preset.spec.{LayerSpec, OpenTerrainGeneratorLayerSpec}

class GuiSettingsLayer(parent: GuiSetupDimensionalLayersPreset, val layer: OpenTerrainGeneratorLayerSpec, index: Int)
  extends GuiBaseSettingsLayer(parent, index) {

  val configYmlH = new ValueHolder[String](layer.toOTGConfigServer.toYamlString)

  override def buildLayerSpec(): LayerSpec = layer.copy(
    seedOverride = toLongSeed(DimensionConfig.fromYamlString(configYmlH.get).Seed),
    configYml = if (hasChanges) Some(configYmlH.get) else None
  )

  override def initGui(): Unit = {
    super.initGui()

    addElement(new OTGConfigPanel(this))

    addLink("Wiki", "https://openterraingen.fandom.com/wiki/GUI_and_Commands", RelativeCoord.alignRight(-10 - fr.getStringWidth("Wiki")), RelativeCoord.verticalCenterMax(0))
  }
}
