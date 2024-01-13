package hohserg.dimensional.layers.gui.settings.cubic.worldtype

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import hohserg.dimensional.layers.Main
import hohserg.dimensional.layers.gui.GuiSelectDimension.{DrawableDim, allDimensions, itemWidth}
import hohserg.dimensional.layers.gui.settings.cubic.worldtype.GuiSelectCompatibleDimension.dimLinesByLenByCubicWorldType
import hohserg.dimensional.layers.gui.{GuiSelectDimension, GuiTileList}
import hohserg.dimensional.layers.preset.CubicWorldTypeLayerSpec
import hohserg.dimensional.layers.preset.CubicWorldTypeLayerSpec._
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import it.unimi.dsi.fastutil.ints.{Int2ObjectLinkedOpenHashMap, Int2ObjectMap, IntSortedSet}
import net.minecraft.world.{DimensionType, WorldServer, WorldType}
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.util.EnumHelper

import scala.collection.JavaConverters._
import scala.collection.mutable

class GuiSelectCompatibleDimension(parent: GuiSettingsLayer)
  extends GuiSelectDimension(parent, dimLinesByLenByCubicWorldType.get(parent.layer.cubicWorldType)) {

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
          GuiTileList.createLinesCache(filterDimensionsByCubicWorldType(key), itemWidth)
        }
      })

  def filterDimensionsByCubicWorldType(worldType: WorldType with ICubicWorldType): Seq[DrawableDim] = {
    val original = AccessorDimensionManager.getWorlds
    try {
      val fakeWorlds = new Int2ObjectLinkedOpenHashMap[WorldServer]

      val worldByDimType: mutable.Map[DimensionType, (WorldServer, IntSortedSet)] =
        DimensionManager.getRegisteredDimensions.asScala
          .map { case (dim, ids) =>
            dim -> (dummyWorld(CubicWorldTypeLayerSpec(worldType, dimensionType1 = dim)) -> ids)
          }

      fakeWorlds.putAll(
        worldByDimType.values.flatMap { case (world, ids) => ids.asScala.map(_ -> world) }.toMap
          .asJava
      )

      AccessorDimensionManager.setWorlds(fakeWorlds)
      allDimensions.filter(d => worldType.hasCubicGeneratorForWorld(worldByDimType(d.dimensionType)._1))

    } catch {
      case e: Throwable =>
        Main.sided.printError("failed to filter dimension types for cubic world type: " + worldType.getName, e)
        allDimensions

    } finally {
      AccessorDimensionManager.setWorlds(original)
    }
  }

  object AccessorDimensionManager {
    def getWorlds: Int2ObjectMap[WorldServer] =
      field.get(null).asInstanceOf[Int2ObjectMap[WorldServer]]

    def setWorlds(v: Int2ObjectMap[WorldServer]): Unit =
      EnumHelper.setFailsafeFieldValue(field, null, v)

    private lazy val field = {
      val r = classOf[DimensionManager].getDeclaredField("worlds")
      r.setAccessible(true)
      r
    }
  }
}
