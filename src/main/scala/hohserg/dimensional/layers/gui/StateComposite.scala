package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.mutable.ListBuffer

@SideOnly(Side.CLIENT)
trait StateComposite {

  def state: ListBuffer[ValueHolder[?]]

  def onStateChanged(): Unit


}
