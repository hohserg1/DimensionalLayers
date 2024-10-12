package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class GuiNumericField[N: Numeric](x: Int, y: Int, maxLen: Int, value: ValueHolder[N], fromString: String => N, h: Int = 18)
                                 (implicit gui: GuiBase)
  extends GuiTextFieldElement[N](x, y, maxLen * gui.fr.getCharWidth('0') + 8, h, value, fromString) {
}
