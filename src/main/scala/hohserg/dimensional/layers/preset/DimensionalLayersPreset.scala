package hohserg.dimensional.layers.preset

import com.google.gson.JsonParseException
import hohserg.dimensional.layers.data.layer.base.Layer
import hohserg.dimensional.layers.{CCWorld, CCWorldServer, Configuration, Main}
import io.github.opencubicchunks.cubicchunks.api.util.IntRange
import net.minecraft.init.Blocks
import net.minecraftforge.common.DimensionManager

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

case class DimensionalLayersPreset(layers: List[LayerSpec], startCubeY: Int = 0) {
  def toLayerSeq(original: CCWorld): Seq[(IntRange, Layer)] = {
    layers
      .foldRight(List[(IntRange, Layer)]() -> startCubeY) {
        case (spec, (acc, lastFreeCubic)) =>
          val layer: Layer = spec.toLayer(lastFreeCubic, original)

          (range(lastFreeCubic, layer.bounds.cubeHeight) -> layer :: acc) -> (lastFreeCubic + layer.bounds.cubeHeight)
      }
      ._1
  }

  private def range(lastFreeCubic: Int, height: Int) = IntRange.of(lastFreeCubic, lastFreeCubic + height - 1)

  def toSettings: String = Serialization.gson.toJson(this)
}

object DimensionalLayersPreset {
  def fromJson(settings: String): DimensionalLayersPreset =
    Try(settings)
      .filter(_.nonEmpty)
      .orElse(Try(Configuration.defaultPreset).filter(_.nonEmpty))
      .map(Serialization.gson.fromJson(_, classOf[DimensionalLayersPreset]))
    match {
      case Failure(exception) =>
        handleError(settings, exception)
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

  private def handleError(preset: String, exception: Throwable): Unit = {
    (exception match {
      case ingore: NoSuchElementException =>
        None
      case badJson: JsonParseException =>
        Some("Malformed json:")
      case unexpected: Throwable =>
        Some("Error while parsing json. Plz report to author")
    }).foreach { humanReadable =>
      Main.sided.printError(humanReadable, preset, exception)
    }
  }
}
