package hohserg.dimensional.layers.gui.settings.otg

import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.GuiTextFieldElement
import hohserg.dimensional.layers.gui.RelativeCoord.{alignLeft, alignTop}
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.settings.GuiBaseSettingsLayer
import hohserg.dimensional.layers.preset.{LayerSpec, OpenTerrainGeneratorLayerSpec}

class GuiSettingsLayer(parent: GuiSetupDimensionalLayersPreset, layer: OpenTerrainGeneratorLayerSpec, index: Int)
  extends GuiBaseSettingsLayer(parent, index) {

  val seedOverrideH = new ValueHolder[String](layer.seedOverride.map(_.toString).getOrElse(""))

  override def buildLayerSpec(): LayerSpec = layer.copy(seedOverride = toLongSeed(seedOverrideH.get))

  override def initGui(): Unit = {
    super.initGui()

    val seedOverrideField = addElement(new GuiTextFieldElement(width - 180, height / 2 - 20 - 20, 170, 20, seedOverrideH, identity))
    addCenteredLabel("seed override:", alignLeft(seedOverrideField.x + seedOverrideField.width / 2), alignTop(seedOverrideField.y - 13), 0xffa0a0a0)
  }
}
