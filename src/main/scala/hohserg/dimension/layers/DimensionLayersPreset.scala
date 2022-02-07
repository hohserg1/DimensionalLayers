package hohserg.dimension.layers

import hohserg.dimension.layers.DimensionLayersPreset.LayerSpec
import hohserg.dimension.layers.DimensionLayersPreset.LayerSpec.{cubicHeightLeftBracket, dimensionTypeLeftBracket, rightBracket}
import hohserg.dimension.layers.Layer.FakeWorldLayer
import hohserg.dimension.layers.fake.world.FakeWorld
import io.github.opencubicchunks.cubicchunks.api.util.IntRange
import net.minecraft.world.{DimensionType, World}
import net.minecraftforge.common.DimensionManager

import scala.collection.JavaConverters.asScalaSetConverter
import scala.util.Try

case class DimensionLayersPreset(layers: List[LayerSpec]) {

  def toJson: String = "[" + layers.map(l => l.toJson).mkString(", ") + "]"

  def toLayerMap: Map[IntRange, World => Layer] =
    layers
      .foldRight(List[(IntRange, World => Layer)]() -> 0) {
        case (LayerSpec(dt, height), (acc, lastFreeCubic)) =>
          ((IntRange.of(lastFreeCubic, lastFreeCubic + height - 1) -> {
            world: World =>
              FakeWorldLayer(FakeWorld(
                dt,
                world.getSeed,
                enableMapFeatures = true,
                hasSkyLight = false
              ))
          }) :: acc) -> (lastFreeCubic + height)
      }._1.toMap
}

object DimensionLayersPreset {
  case class LayerSpec(dimensionType: DimensionType, cubicHeight: Int = 16) {
    def toJson: String =
      "{" +
        dimensionTypeLeftBracket + dimensionType.getName + rightBracket + ", " +
        cubicHeightLeftBracket + cubicHeight + rightBracket +
        "}"
  }

  object LayerSpec {
    val dimensionTypeLeftBracket = "dimensionType=\""
    val cubicHeightLeftBracket = "cubicHeight=\""
    val rightBracket = "\""

    def fromJson(json: String): LayerSpec = {
      val dimensionTypeStart = json.indexOf(dimensionTypeLeftBracket) + dimensionTypeLeftBracket.length
      val dimensionTypeEnd = json.indexOf(rightBracket, dimensionTypeStart)
      val cubicHeightStart = json.indexOf(cubicHeightLeftBracket) + cubicHeightLeftBracket.length
      val cubicHeightEnd = json.indexOf(rightBracket, cubicHeightStart)
      LayerSpec(
        DimensionType.byName(json.substring(dimensionTypeStart, dimensionTypeEnd)),
        json.substring(cubicHeightStart, cubicHeightEnd).toInt
      )
    }

  }

  lazy val mixAllDims = DimensionLayersPreset(
    DimensionManager.getRegisteredDimensions.keySet().asScala.map(LayerSpec(_)).toList
  )

  def fromJson(json: String): DimensionLayersPreset =
    if (json == "mixAllDims")
      mixAllDims
    else
      Try(DimensionLayersPreset(
        json.substring(json.indexOf('[') + 1, json.lastIndexOf(']'))
          .replaceAll("\\s", "")
          .split(',')
          .map(LayerSpec.fromJson)
          .toList
      )).getOrElse(mixAllDims)

}
