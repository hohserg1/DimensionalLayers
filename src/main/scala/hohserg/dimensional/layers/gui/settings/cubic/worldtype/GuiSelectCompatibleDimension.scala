package hohserg.dimensional.layers.gui.settings.cubic.worldtype

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import hohserg.dimensional.layers.gui.GuiSelectDimension.{DrawableDim, allDimensions, itemWidth}
import hohserg.dimensional.layers.gui.{GuiSelectDimension, GuiTileList}
import hohserg.dimensional.layers.preset.CubicWorldTypeLayerSpec
import hohserg.dimensional.layers.preset.CubicWorldTypeLayerSpec._
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import net.minecraft.world.WorldType

class GuiSelectCompatibleDimension(parent: GuiSettingsLayer)
  extends GuiSelectDimension(parent /*, dimLinesByLenByCubicWorldType.get(parent.layer.cubicWorldType)*/) {

  override def onSelected(item: DrawableDim): Unit = {
    parent.dimensionTypeH.set(item.dimensionType)
    back()
  }
}

object GuiSelectCompatibleDimension {
  val dimLinesByLenByCubicWorldType: LoadingCache[WorldType with ICubicWorldType, LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[DrawableDim]]]] =
    CacheBuilder.newBuilder()
      .build(new CacheLoader[WorldType with ICubicWorldType, LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[DrawableDim]]]] {
        override def load(key: WorldType with ICubicWorldType): LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[DrawableDim]]] = {
          GuiTileList.createLinesCache(
            allDimensions.filter(d => key.hasCubicGeneratorForWorld(dummyWorld(CubicWorldTypeLayerSpec(key, dimensionType1 = d.dimensionType)))),
            itemWidth
          )
        }
      })
}
