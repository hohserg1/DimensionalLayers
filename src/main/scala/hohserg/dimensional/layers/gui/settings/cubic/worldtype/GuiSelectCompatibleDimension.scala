package hohserg.dimensional.layers.gui.settings.cubic.worldtype

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import hohserg.dimensional.layers.Main
import hohserg.dimensional.layers.gui.GuiSelectDimension.{DrawableDim, allDimensions, itemWidth}
import hohserg.dimensional.layers.gui.settings.cubic.worldtype.GuiSelectCompatibleDimension.dimLinesByLenByCubicWorldType
import hohserg.dimensional.layers.gui.{GuiSelectDimension, GuiTileList}
import hohserg.dimensional.layers.lens.DimensionManagerLens
import hohserg.dimensional.layers.preset.spec.CubicWorldTypeLayerSpec
import hohserg.dimensional.layers.worldgen.proxy.server.BaseWorldServer
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import it.unimi.dsi.fastutil.ints.{Int2ObjectLinkedOpenHashMap, Int2ObjectMap, IntSortedSet}
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.storage.WorldInfo
import net.minecraft.world.{DimensionType, GameType, WorldServer, WorldSettings, WorldType}
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.mutable
import scala.jdk.CollectionConverters.*

@SideOnly(Side.CLIENT)
class GuiSelectCompatibleDimension(parent: GuiSettingsLayer)
  extends GuiSelectDimension(parent, dimLinesByLenByCubicWorldType.get(parent.layer.cubicWorldType)) {

  override def onSelected(item: DrawableDim): Unit = {
    parent.dimensionTypeH.set(item.dimensionType)
    back()
  }
}

@SideOnly(Side.CLIENT)
object GuiSelectCompatibleDimension {
  val dimLinesByLenByCubicWorldType: LoadingCache[WorldType & ICubicWorldType, LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[DrawableDim]]]] =
    CacheBuilder.newBuilder()
                .build(new CacheLoader[WorldType & ICubicWorldType, LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[DrawableDim]]]] {
                  override def load(key: WorldType & ICubicWorldType): LoadingCache[Integer, Seq[GuiTileList.GuiTileLine[DrawableDim]]] = {
                    GuiTileList.createLinesCache(filterDimensionsByCubicWorldType(key), itemWidth)
                  }
                })

  def filterDimensionsByCubicWorldType(worldType: WorldType & ICubicWorldType): Seq[DrawableDim] = {
    val original = DimensionManagerLens.worlds.get(null)
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

      DimensionManagerLens.worlds.set(null, fakeWorlds)
      allDimensions.filter(d => worldType.hasCubicGeneratorForWorld(worldByDimType(d.dimensionType)._1))

    } catch {
      case e: Throwable =>
        Main.sided.printError("failed to filter dimension types for cubic world type: ", worldType.getName, e)
        allDimensions

    } finally {
      DimensionManagerLens.worlds.set(null, original)
    }
  }

  def dummyWorld(spec: CubicWorldTypeLayerSpec, gameType: GameType = GameType.SURVIVAL, isMapFeaturesEnabled: Boolean = true): WorldServer = {
    new BaseWorldServer(
      null,
      new WorldInfo(
        new WorldSettings(
          spec.seedOverride.getOrElse(0),
          gameType,
          isMapFeaturesEnabled,
          false,
          spec.cubicWorldType
        ).setGeneratorOptions(spec.worldTypePreset),
        "dummyWorld---"
      ),
      spec.dimensionType1.createDimension(),
      null
    ) {
      override def createChunkProvider(): IChunkProvider = ???

      override def isChunkLoaded(x: Int, z: Int, allowEmpty: Boolean): Boolean = ???
    }.asInstanceOf[WorldServer]
  }
}
