package hohserg.dimensional.layers.gui.whole.world

import com.google.common.cache.LoadingCache
import hohserg.dimensional.layers.Main
import hohserg.dimensional.layers.gui.GuiSelectDimension.itemWidth
import hohserg.dimensional.layers.gui.GuiTileList.GuiTileLine
import hohserg.dimensional.layers.gui.preset.GuiSetupDimensionalLayersPreset
import hohserg.dimensional.layers.gui.{GuiBase, GuiTileList, IconUtils}
import hohserg.dimensional.layers.preset.DimensionalLayersPreset

class GuiSelectRealDimensionForEdit(parent: GuiSetupDimensionalLayersPreset)
  extends GuiBase(parent)
    with GuiTileList.SelectHandler[RealDimensionListElement] {
  var preset: DimensionalLayersPreset = parent.layersList.actualPreset

  override def initGui(): Unit = {
    super.initGui()

    val linesCache: LoadingCache[Integer, Seq[GuiTileLine[RealDimensionListElement]]] =
      GuiTileList.createLinesCache(preset.realDimensionToLayers.toSeq.sortBy { case (dimId, _) => dimId }.map(RealDimensionDrawable.apply.tupled) ++ Seq(AddNew), itemWidth)

    addElement(new GuiTileList(this, x = 10, y = 10, availableWidth = width - 110, height = height - 20, IconUtils.width, linesCache)())
  }

  override def onSelected(e: RealDimensionListElement): Unit = {
    e match {
      case AddNew =>
        show(new GuiNewRealDimension(_))()
      case realDimensionId: RealDimensionDrawable =>
        if (remove.isHovering(using realDimensionId)) {
          if (preset.realDimensionToLayers.size > 1) {
            val nextPreset = preset.copy(realDimensionToLayers = preset.realDimensionToLayers.removed(realDimensionId.id))
            if (parent.currentRealDimension == realDimensionId.id)
              parent.currentRealDimension = nextPreset.realDimensionToLayers.keys.head
            parent.initFromJson(nextPreset.toSettings)
            preset = nextPreset
            scheduleToRebuild = true
          } else
            Main.sided.printSimpleError("One real dimension should be configured", "add other real dimension before removal this one")
        } else {
          parent.currentRealDimension = realDimensionId.id
          back()
        }
    }
  }

  var scheduleToRebuild = false

  override def drawScreenPost(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    super.drawScreenPost(mouseX, mouseY, partialTicks)
    if (scheduleToRebuild) {
      scheduleToRebuild = false
      initGui()
    }
  }

}
