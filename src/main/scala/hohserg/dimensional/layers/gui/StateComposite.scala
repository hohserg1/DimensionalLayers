package hohserg.dimensional.layers.gui

import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder

import scala.collection.mutable.ListBuffer

trait StateComposite {

  def state: ListBuffer[ValueHolder[_]]

  def onStateChanged(): Unit


}
