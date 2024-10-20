package hohserg.dimensional.layers.gui

import com.google.common.cache.LoadingCache
import hohserg.dimensional.layers.gui.GuiSelectDimension.DrawableDim
import hohserg.dimensional.layers.gui.IconUtils._
import hohserg.dimensional.layers.preset.DimensionalLayersPreset
import net.minecraft.world.DimensionType
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
abstract class GuiSelectDimension(parent: GuiBase,
                                  linesCache: LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[DrawableDim]]] = GuiSelectDimension.dimLinesByLen)
  extends GuiBase(parent) with GuiTileList.SelectHandler[DrawableDim] {

  override def initGui(): Unit = {
    super.initGui()
    addButton(new GuiClickableButton(width - 100, height - 30, 90, 20, "Cancel")(back))
    addElement(new GuiTileList(this, x = 10, y = 10, availableWidth = width - 110, height = height - 20, IconUtils.width, linesCache)())
  }
}

object GuiSelectDimension {

  final val itemWidth = IconUtils.width


  lazy val allDimensions: Seq[DrawableDim] = DimensionalLayersPreset.availableDims.map(DrawableDim).toIndexedSeq

  val dimLinesByLen: LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[DrawableDim]]] = GuiTileList.createLinesCache(allDimensions, itemWidth)

  case class DrawableDim(dimensionType: DimensionType) extends Drawable {
    override def draw(minX: Int, minY: Int, maxX: Int, maxY: Int): Unit =
      drawLogo(dimensionType, minX, minY)

    override def tooltip: String =
      makeDimensionTypeLabel(dimensionType)
  }
}
