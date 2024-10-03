package hohserg.dimensional.layers.gui.preset.list

import com.google.common.base.Predicate
import hohserg.dimensional.layers.data.LayerMap
import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.{GuiBase, GuiNumericField}

import scala.util.Try

class GuiStartCubeYField(x: Int, y: Int, value: ValueHolder[Int])
                        (implicit gui: GuiBase)
  extends GuiNumericField[Int](x, y, LayerMap.minCubeY.toString.length, value, _.toInt) {

  setValidator(new Predicate[String] {
    override def apply(input: String): Boolean = input.isEmpty || input == "-" || Try(input.toInt).isSuccess
  })

  override def setFocused(isFocusedIn: Boolean): Unit = {
    super.setFocused(isFocusedIn)
    if (!isFocusedIn)
      setText(value.get.toString)
  }

}
