package hohserg.dimensional.layers.preset.spec

import hohserg.dimensional.layers.CCWorld
import hohserg.dimensional.layers.data.layer.base.Layer
import hohserg.dimensional.layers.gui.preset.list._
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

trait LayerSpec {
  def height: Int

  def toLayer: (Int, this.type, CCWorld) => Layer

  def toLayer(startFromCubeY: Int, original: CCWorld): Layer = toLayer(startFromCubeY, this, original)

  @SideOnly(Side.CLIENT)
  def toGuiLayerEntry(parent: GuiLayersList): GuiLayerEntry

}