package hohserg.dimensional.layers.preset

import com.google.gson.JsonParseException
import hohserg.dimensional.layers.Configuration
import hohserg.dimensional.layers.worldgen.{DimensionLayer, Layer, SolidLayer}
import io.github.opencubicchunks.cubicchunks.api.util.IntRange
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.toasts.SystemToast
import net.minecraft.init.Blocks
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager

import scala.collection.JavaConverters.asScalaSetConverter
import scala.util.{Failure, Success, Try}

case class DimensionalLayersPreset(layers: List[LayerSpec]) {
  def toLayerMap: Map[IntRange, World => Layer] =
    layers
      .foldRight(List[(IntRange, World => Layer)]() -> 0) {
        case (spec: DimensionLayerSpec, (acc, lastFreeCubic)) =>
          (range(lastFreeCubic, spec.height) -> (new DimensionLayer(_: World, spec, lastFreeCubic)) :: acc) -> (lastFreeCubic + spec.height)

        case (SolidLayerSpec(filler, height, biome), (acc, lastFreeCubic)) =>
          (range(lastFreeCubic, height) -> { _: World => SolidLayer(filler, biome, lastFreeCubic, height) } :: acc) -> (lastFreeCubic + height)
      }._1.toMap

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

  private def mixedPreset =
    DimensionalLayersPreset(
      DimensionManager.getRegisteredDimensions.keySet().asScala.map(DimensionLayerSpec(_)).toList
        :+ SolidLayerSpec(Blocks.BEDROCK.getDefaultState, 1)
    )

  private def handleError(exception: Throwable): Unit = {
    println("DimensionalLayersPreset json parsing error")
    exception.printStackTrace()
    (exception match {
      case ignore: NoSuchElementException =>
        None
      case badJson: JsonParseException =>
        Some("Malformed json:")
      case unexpected: Throwable =>
        Some("Error while parsing json. Plz report to author")
    }).foreach { title =>
      Minecraft.getMinecraft.getToastGui.add(new SystemToast(
        SystemToast.Type.NARRATOR_TOGGLE,
        new TextComponentString(title),
        new TextComponentString(exception.getMessage + "\nfull stacktrace in log")
      ))
    }
  }
}
