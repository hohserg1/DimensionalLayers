package hohserg.dimensional.layers.preset.spec

import hohserg.dimensional.layers.CCWorld
import hohserg.dimensional.layers.data.layer.base.Layer
import hohserg.dimensional.layers.data.layer.cubic_world_type.CubicWorldTypeLayer
import hohserg.dimensional.layers.data.layer.mystcraft.MystcraftDimensionLayer
import hohserg.dimensional.layers.data.layer.solid.SolidLayer
import hohserg.dimensional.layers.data.layer.vanilla_dimension.VanillaDimensionLayer
import hohserg.dimensional.layers.gui.preset.list.*
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Biomes
import net.minecraft.world.biome.Biome
import net.minecraft.world.{DimensionType, WorldType}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

sealed trait LayerSpec {
  def height: Int

  def additionalFeatures: Seq[AdditionalFeature]

  def toLayer: (Int, this.type, CCWorld) => Layer

  def toLayer(startFromCubeY: Int, original: CCWorld): Layer = toLayer(startFromCubeY, this, original)

  @SideOnly(Side.CLIENT)
  def toGuiLayerEntry(parent: GuiLayersList): GuiLayerEntry
}

case class CubeOffsets(topOffset: Int, bottomOffset: Int) {
  val height: Int = 16 - topOffset - bottomOffset
}

case class DimensionLayerSpec(dimensionType: DimensionType,
                              seedOverride: Option[Long] = None,
                              offsets: CubeOffsets = CubeOffsets(topOffset = 0, bottomOffset = 0),
                              worldType: WorldType = WorldType.DEFAULT, worldTypePreset: String = "",
                              additionalFeatures: Seq[AdditionalFeature] = Seq.empty
                             ) extends LayerSpec {

  override def height: Int = offsets.height

  override val toLayer = VanillaDimensionLayer.apply

  @SideOnly(Side.CLIENT)
  override def toGuiLayerEntry(parent: GuiLayersList): GuiLayerEntry = new GuiDimensionLayerEntry(parent, this)
}

case class CubicWorldTypeLayerSpec(cubicWorldType: WorldType & ICubicWorldType, worldTypePreset: String = "",
                                   dimensionType1: DimensionType = DimensionType.OVERWORLD,
                                   minCubeY: Int = 0,
                                   maxCubeY: Int = 32,
                                   seedOverride: Option[Long] = None,
                                   additionalFeatures: Seq[AdditionalFeature] = Seq.empty
                                  ) extends LayerSpec {

  override val toLayer = CubicWorldTypeLayer.apply

  override val height: Int = maxCubeY - minCubeY + 1


  @SideOnly(Side.CLIENT)
  override def toGuiLayerEntry(parent: GuiLayersList): GuiLayerEntry = new GuiCubicWorldTypeLayerEntry(parent, this)
}

case class SolidLayerSpec(filler: IBlockState, height: Int, biome: Biome = Biomes.PLAINS,
                          additionalFeatures: Seq[AdditionalFeature] = Seq.empty) extends LayerSpec {
  override val toLayer = SolidLayer.apply

  @SideOnly(Side.CLIENT)
  override def toGuiLayerEntry(parent: GuiLayersList): GuiLayerEntry = new GuiSolidLayerEntry(parent, this)
}

case class MystcraftLayerSpec(seedOverride: Option[Long] = None,
                              instability: Short = 0,
                              symbols: Seq[String] = Seq("mystcraft:terrainamplified"),
                              offsets: CubeOffsets = CubeOffsets(topOffset = 0, bottomOffset = 0),
                              additionalFeatures: Seq[AdditionalFeature] = Seq.empty) extends LayerSpec {

  override def height: Int = offsets.height

  override val toLayer = MystcraftDimensionLayer.apply

  override def toGuiLayerEntry(parent: GuiLayersList): GuiLayerEntry = new GuiMystcraftLayerEntry(parent, this)
}