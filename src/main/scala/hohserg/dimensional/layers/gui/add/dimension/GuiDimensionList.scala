package hohserg.dimensional.layers.gui.add.dimension

import com.google.common.cache.LoadingCache
import hohserg.dimensional.layers.gui.add.dimension.GuiDimensionList.{dimLinesByLen, itemWidth}
import hohserg.dimensional.layers.gui.{DimensionClientUtils, Drawable, GuiTileList}
import net.minecraft.world.DimensionType
import net.minecraftforge.common.DimensionManager

import scala.collection.JavaConverters.asScalaSetConverter

object GuiDimensionList {
  final val itemWidth = DimensionClientUtils.width


  lazy val allDimensions: Seq[DrawableDim] = DimensionManager.getRegisteredDimensions.keySet().asScala.map(DrawableDim).toIndexedSeq

  val dimLinesByLen: LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[DrawableDim]]] = GuiTileList.createLinesCache(allDimensions, itemWidth)

  case class DrawableDim(dimensionType: DimensionType) extends Drawable {
    override def draw(minX: Int, minY: Int, maxX: Int, maxY: Int): Unit =
      DimensionClientUtils.drawLogo(dimensionType, minX, minY)

    override def tooltip: String =
      DimensionClientUtils.getDisplayName(dimensionType)
  }

}

class GuiDimensionList(parent: GuiAddLayer, availableWidth: Int) extends GuiTileList(parent, availableWidth, itemWidth, dimLinesByLen)() {
}
