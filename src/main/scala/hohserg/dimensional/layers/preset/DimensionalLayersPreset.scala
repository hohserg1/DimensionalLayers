package hohserg.dimensional.layers.preset

import com.google.gson.JsonParseException
import hohserg.dimensional.layers.worldgen.{CubicWorldTypeLayer, DimensionLayer, Layer, SolidLayer}
import hohserg.dimensional.layers.{CCWorld, Configuration, Main}
import io.github.opencubicchunks.cubicchunks.api.util.IntRange
import net.minecraft.init.Blocks
import net.minecraftforge.common.DimensionManager

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

case class DimensionalLayersPreset(layers: List[LayerSpec]) {
  def toLayerMap(original: CCWorld): Map[Int, Layer] =
    layers
      .foldRight(List[(IntRange, Layer)]() -> 0) {
        case (spec, (acc, lastFreeCubic)) =>

          val layer = spec match {
            case spec: DimensionLayerSpec =>
              new DimensionLayer(original, spec, lastFreeCubic)

            case SolidLayerSpec(filler, height, biome) =>
              SolidLayer(filler, biome, lastFreeCubic, height)

            case spec: CubicWorldTypeLayerSpec =>
              new CubicWorldTypeLayer(original, spec, lastFreeCubic)
          }

          (range(lastFreeCubic, layer.height) -> layer :: acc) -> (lastFreeCubic + layer.height)
      }
      ._1
      .toMap
      .flatMap { case (range, layer) =>
        for (i <- range.getMin to range.getMax)
          yield i -> layer
      }

  private def range(lastFreeCubic: Int, height: Int) = IntRange.of(lastFreeCubic, lastFreeCubic + height - 1)

  def toSettings: String = Serialization.gson.toJson(this)
}

object DimensionalLayersPreset {
  def apply(settings: String): DimensionalLayersPreset =
    Try(settings)
      .filter(_.nonEmpty)
      .orElse(Try(Configuration.defaultPreset).filter(_.nonEmpty))
      .map(Serialization.gson.fromJson(_, classOf[DimensionalLayersPreset]))
    match {
      case Failure(exception) =>
        handleError(exception)
        mixedPreset
      case Success(value) =>
        value
    }

  lazy val mixedPresetTop: List[DimensionLayerSpec] =
    DimensionManager.getRegisteredDimensions.keySet().asScala.map(DimensionLayerSpec(_)).toList

  def mixedPreset =
    DimensionalLayersPreset(
      scala.util.Random.shuffle(mixedPresetTop)
        :+ SolidLayerSpec(Blocks.BEDROCK.getDefaultState, 1)
    )

  private def handleError(exception: Throwable): Unit = {
    (exception match {
      case emptyString: NoSuchElementException =>
        Some("Json is empty string, will be used mixed preset")
      case badJson: JsonParseException =>
        Some("Malformed json:")
      case unexpected: Throwable =>
        Some("Error while parsing json. Plz report to author")
    }).foreach { humanReadable =>
      Main.proxy.printError(humanReadable, exception)
    }
  }
}
