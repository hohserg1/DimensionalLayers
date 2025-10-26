package hohserg.dimensional.layers.gui.preset

import com.google.common.collect.ImmutableList
import hohserg.dimensional.layers.gui.whole.world.GuiSelectRealDimensionForEdit
import hohserg.dimensional.layers.gui.{DrawableArea, GuiTexturedButton}

import java.awt.Rectangle

class GuiChangeRealDimensionButton(x: Int, y: Int)(implicit gui: GuiSetupDimensionalLayersPreset)
  extends GuiTexturedButton(x, y, 33, 33, "", new Rectangle(2, 96, 33, 33))(gui.show(new GuiSelectRealDimensionForEdit(_)))
    with DrawableArea.Container {

  def renderTooltip(mouseX: Int, mouseY: Int): Unit = {
    if (area.isHovering)
      gui.drawHoveringText(ImmutableList.of("currently editing layers of real dimension " + gui.currentRealDimension, "click to switch to other"), mouseX, mouseY - 10)
  }
}