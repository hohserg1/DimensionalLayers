package hohserg.dimensional.layers

import com.google.gson._
import hohserg.dimensional.layers.DimensionLayersPreset.{DimensionLayerSpec, LayerSpec, SolidLayerSpec}
import hohserg.dimensional.layers.worldgen.{DimensionLayer, Layer, SolidLayer}
import io.github.opencubicchunks.cubicchunks.api.util.IntRange
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.init.{Biomes, Blocks}
import net.minecraft.util.ResourceLocation
import net.minecraft.world.biome.Biome
import net.minecraft.world.{DimensionType, World}
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.common.registry.ForgeRegistries

import java.lang.reflect.Type
import scala.collection.JavaConverters.{asScalaIteratorConverter, asScalaSetConverter, mapAsScalaMapConverter}
import scala.util.Try

case class DimensionLayersPreset(layers: List[LayerSpec]) {
  def toLayerMap: Map[IntRange, World => Layer] =
    layers
      .foldRight(List[(IntRange, World => Layer)]() -> 0) {
        case (spec: DimensionLayerSpec, (acc, lastFreeCubic)) =>
          (range(lastFreeCubic, spec.height) -> (new DimensionLayer(_: World, spec, lastFreeCubic)) :: acc) -> (lastFreeCubic + spec.height)

        case (SolidLayerSpec(filler, biome, height), (acc, lastFreeCubic)) =>
          (range(lastFreeCubic, height) -> { _: World => SolidLayer(filler, lastFreeCubic, height) } :: acc) -> (lastFreeCubic + height)
      }._1.toMap

  private def range(lastFreeCubic: Int, height: Int) = IntRange.of(lastFreeCubic, lastFreeCubic + height - 1)

  def toSettings: String = DimensionLayersPreset.gson.toJson(this)
}

object DimensionLayersPreset {
  def apply(settings: String): DimensionLayersPreset =
    Try(settings)
      .filter(_.nonEmpty)
      .orElse(Try(Configuration.defaultPreset).filter(_.nonEmpty))
      .map(gson.fromJson(_, classOf[DimensionLayersPreset]))
      .getOrElse(DimensionLayersPreset(
        DimensionManager.getRegisteredDimensions.keySet().asScala.map(DimensionLayerSpec(_)).toList :+ SolidLayerSpec(Blocks.BEDROCK.getDefaultState)
      ))


  sealed trait LayerSpec {
    def height: Int
  }

  case class DimensionLayerSpec(dimensionType: DimensionType, seedOverride: Option[Long] = None) extends LayerSpec {
    override def height: Int = 16
  }

  case class SolidLayerSpec(filler: IBlockState, biome: Biome = Biomes.PLAINS, height: Int = 1) extends LayerSpec


  private val gson: Gson =
    (new GsonBuilder)
      .registerTypeHierarchyAdapter(classOf[LayerSpec], LayerSpecSerializer)
      .registerTypeHierarchyAdapter(classOf[DimensionLayersPreset], Serializer)
      .create()

  private object Serializer extends JsonSerializer[DimensionLayersPreset] with JsonDeserializer[DimensionLayersPreset] {
    override def serialize(src: DimensionLayersPreset, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      val r = new JsonArray
      src.layers.foreach(l => r.add(context.serialize(l)))
      r
    }

    override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DimensionLayersPreset =
      DimensionLayersPreset(json.getAsJsonArray.iterator().asScala.map(je => context.deserialize[LayerSpec](je, classOf[LayerSpec])).toList)
  }

  private object LayerSpecSerializer extends JsonSerializer[LayerSpec] with JsonDeserializer[LayerSpec] {
    override def serialize(src: LayerSpec, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      val r = new JsonObject
      src match {
        case DimensionLayerSpec(dimensionType, seedOverride) =>
          r.add("dimensionType", new JsonPrimitive(dimensionType.getName))
          seedOverride.foreach(seed => r.add("seedOverride", new JsonPrimitive(seed)))

        case SolidLayerSpec(filler, biome, height) =>
          r.add("filler", new JsonPrimitive(filler.getBlock.getRegistryName.toString))
          if (filler != filler.getBlock.getDefaultState) {
            val jsonProps = new JsonObject
            filler.getProperties.asScala.foreach { case (p, v) => jsonProps.add(p.getName, new JsonPrimitive(p.getName(v.asInstanceOf))) }
            r.add("properties", jsonProps)
          }
          r.add("height", new JsonPrimitive(height))
          if (biome != Biomes.PLAINS) {
            r.add("biome", new JsonPrimitive(biome.getRegistryName.toString))
          }
      }
      r
    }

    override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LayerSpec = {
      val jsonObject = json.getAsJsonObject
      if (jsonObject.has("dimensionType"))
        DimensionLayerSpec(
          DimensionType.byName(jsonObject.getAsJsonPrimitive("dimensionType").getAsString),
          if (jsonObject.has("seedOverride"))
            Some(jsonObject.getAsJsonPrimitive("seedOverride").getAsLong)
          else
            None
        )
      else {
        val block = Block.getBlockFromName(jsonObject.getAsJsonPrimitive("filler").getAsString)

        SolidLayerSpec(if (jsonObject.has("properties")) {
          var state = block.getDefaultState
          val jsonProps = jsonObject.getAsJsonObject("properties")
          jsonProps.entrySet().asScala.foreach { e =>
            val propName = e.getKey
            val valueName = e.getValue.getAsString
            val p = block.getBlockState.getProperty(propName)
            val maybeValue = p.parseValue(valueName)
            if (maybeValue.isPresent) {
              state = state.withProperty(p, maybeValue.get().asInstanceOf)
            }
          }
          state
        } else
          block.getDefaultState, if (jsonObject.has("biome")) {
          ForgeRegistries.BIOMES.getValue(new ResourceLocation(jsonObject.getAsJsonPrimitive("biome").getAsString))
        } else
          Biomes.PLAINS, jsonObject.getAsJsonPrimitive("height").getAsInt)
      }
    }
  }
}
