package hohserg.dimensional.layers.preset

import com.google.gson.JsonParseException
import hohserg.dimensional.layers.data.LayerMap
import hohserg.dimensional.layers.data.layer.base.Layer
import hohserg.dimensional.layers.preset.DimensionalLayersPreset.IllegalPresetException
import hohserg.dimensional.layers.preset.spec.{DimensionLayerSpec, LayerSpec, SolidLayerSpec}
import hohserg.dimensional.layers.{CCWorld, Configuration, Main}
import io.github.opencubicchunks.cubicchunks.api.util.IntRange
import net.minecraft.init.{Biomes, Blocks}
import net.minecraft.world.DimensionType

import scala.util.{Failure, Success, Try}

case class DimensionalLayersPreset(layers: List[LayerSpec], startCubeY: Int = 0)  {

  if (layers.isEmpty)
    throw new IllegalPresetException("no layers available in preset, need to add at least one")

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

  def toSettings: String = Serialization.toJson(this)
}

object DimensionalLayersPreset {
  def fromJson(settings: String): DimensionalLayersPreset =
    Try(settings)
      .filter(_.nonEmpty)
      .orElse(Try(Configuration.defaultPreset).filter(_.nonEmpty))
      .map(Serialization.fromJson)
      .filter(_ != null)
    match {
      case Failure(exception) =>
        handleError(settings, exception)
        mixedPreset
      case Success(value) =>
        value
    }

  def isNotVanillaDim(dimensionType: DimensionType): Boolean =
    dimensionType != DimensionType.OVERWORLD && dimensionType != DimensionType.NETHER && dimensionType != DimensionType.THE_END

  lazy val availableDims: Seq[DimensionType] = DimensionType.values().toSeq.filter(dt => Try(dt.createDimension()).isSuccess)

  lazy val mixedPresetTop: List[DimensionLayerSpec] =
    availableDims
      .filter(isNotVanillaDim)
      .map(DimensionLayerSpec(_))
      .toList

  def mixedPreset =
    DimensionalLayersPreset(
      scala.util.Random.shuffle(mixedPresetTop) ++
        List(DimensionLayerSpec(DimensionType.THE_END), DimensionLayerSpec(DimensionType.OVERWORLD), DimensionLayerSpec(DimensionType.NETHER, topOffset = 8))
        :+ SolidLayerSpec(Blocks.NETHERRACK.getDefaultState, 0 - LayerMap.minCubeY - 1, Biomes.HELL)
        :+ SolidLayerSpec(Blocks.BEDROCK.getDefaultState, 1),
      startCubeY = LayerMap.minCubeY
    )

  private def handleError(preset: String, exception: Throwable): Unit = {
    (exception match {
      case ingore: NoSuchElementException =>
        None
      case badPreset: IllegalPresetException =>
        Some("Malformed preset:")
      case badJson: JsonParseException =>
        Some("Malformed json:")
      case unexpected: Throwable =>
        Some("Error while parsing json. Plz report to author")
    }).foreach { humanReadable =>
      Main.sided.printError(humanReadable, preset, exception)
    }
  }

  class IllegalPresetException(msg: String) extends IllegalArgumentException(msg)
}
