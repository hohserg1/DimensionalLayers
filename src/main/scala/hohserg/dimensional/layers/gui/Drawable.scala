package hohserg.dimensional.layers.gui

import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
trait Drawable {
  def draw(minX: Int, minY: Int, maxX: Int, maxY: Int): Unit

  def tooltip: String

}
