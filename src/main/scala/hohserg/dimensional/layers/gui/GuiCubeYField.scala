package hohserg.dimensional.layers.gui

import com.google.common.base.Predicate
import hohserg.dimensional.layers.data.LayerMap
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.util.Try

@SideOnly(Side.CLIENT)
class GuiCubeYField(x: Int, y: Int, value: ValueHolder[Int])
                   (implicit gui: GuiBase)
  extends GuiNumericField[Int](x, y, LayerMap.minCubeY.toString.length, value, _.toInt) {

  setValidator((input: String) => input.isEmpty || input == "-" || Try(input.toInt).isSuccess)

  override def setFocused(isFocusedIn: Boolean): Unit = {
    super.setFocused(isFocusedIn)
    if (!isFocusedIn)
      setText(value.get.toString)
  }

}
