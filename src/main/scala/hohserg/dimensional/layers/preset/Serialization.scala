package hohserg.dimensional.layers.preset

import com.google.gson._
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Biomes
import net.minecraft.util.ResourceLocation
import net.minecraft.world.{DimensionType, WorldType}
import net.minecraftforge.fml.common.registry.ForgeRegistries

import java.lang.reflect.Type
import scala.collection.JavaConverters.{asScalaIteratorConverter, asScalaSetConverter, mapAsScalaMapConverter}

object Serialization {
  val gson: Gson =
    (new GsonBuilder)
      .registerTypeHierarchyAdapter(classOf[LayerSpec], LayerSpecSerializer)
      .registerTypeHierarchyAdapter(classOf[DimensionalLayersPreset], Serializer)
      .create()

  private object Serializer extends JsonSerializer[DimensionalLayersPreset] with JsonDeserializer[DimensionalLayersPreset] {
    override def serialize(src: DimensionalLayersPreset, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      val r = new JsonArray
      src.layers.foreach(l => r.add(context.serialize(l)))
      r
    }

    override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DimensionalLayersPreset =
      DimensionalLayersPreset(json.getAsJsonArray.iterator().asScala.map(je => context.deserialize[LayerSpec](je, classOf[LayerSpec])).toList)
  }

  private object LayerSpecSerializer extends JsonSerializer[LayerSpec] with JsonDeserializer[LayerSpec] {

    final val dimensionTypeKey = "dimensionType"
    final val topOffsetKey = "topOffset"
    final val bottomOffsetKey = "bottomOffset"
    final val seedOverrideKey = "seedOverride"
    final val worldTypeKey = "worldType"
    final val worldTypePresetKey = "worldTypePreset"
    final val biomeKey = "biome"
    final val heightKey = "height"
    final val fillerKey = "filler"
    final val propertiesKey = "properties"

    override def serialize(src: LayerSpec, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      val r = new JsonObject
      src match {
        case DimensionLayerSpec(dimensionType, seedOverride, topOffset, bottomOffset, worldType, worldTypePreset) =>
          r.add(dimensionTypeKey, new JsonPrimitive(dimensionType.getName))
          seedOverride.foreach(seed => r.add(seedOverrideKey, new JsonPrimitive(seed)))

          if (topOffset > 0)
            r.add(topOffsetKey, new JsonPrimitive(topOffset))

          if (bottomOffset > 0)
            r.add(bottomOffsetKey, new JsonPrimitive(bottomOffset))

          if (worldType != WorldType.DEFAULT)
            r.add(worldTypeKey, new JsonPrimitive(worldType.getName))

          if (worldTypePreset.nonEmpty)
            r.add(worldTypePresetKey, new JsonPrimitive(worldTypePreset))


        case SolidLayerSpec(filler, biome, height) =>
          serializeBlockState(r, filler)
          r.add(heightKey, new JsonPrimitive(height))
          if (biome != Biomes.PLAINS) {
            r.add(biomeKey, new JsonPrimitive(biome.getRegistryName.toString))
          }
      }
      r
    }

    private def serializeBlockState(r: JsonObject, filler: IBlockState) = {
      r.add(fillerKey, new JsonPrimitive(filler.getBlock.getRegistryName.toString))
      if (filler != filler.getBlock.getDefaultState) {
        val jsonProps = new JsonObject
        filler.getProperties.asScala.foreach { case (p, v) => jsonProps.add(p.getName, new JsonPrimitive(p.getName(v.asInstanceOf))) }
        r.add(propertiesKey, jsonProps)
      }
    }

    override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LayerSpec = {
      val jsonObject = json.getAsJsonObject
      if (jsonObject.has(dimensionTypeKey))
        DimensionLayerSpec(
          deserializeDimType(jsonObject),
          deserializeSeed(jsonObject),
          deserializeOffset(jsonObject, topOffsetKey),
          deserializeOffset(jsonObject, bottomOffsetKey),
          deserializeWorldType(jsonObject),
          if (jsonObject.has(worldTypePresetKey)) jsonObject.getAsJsonPrimitive(worldTypePresetKey).getAsString else ""
        )
      else {
        SolidLayerSpec(
          deserializeBlockState(jsonObject),
          deserializeBiome(jsonObject),
          jsonObject.getAsJsonPrimitive(heightKey).getAsInt)
      }
    }

    def deserializeWorldType(jsonObject: JsonObject): WorldType =
      if (jsonObject.has(worldTypeKey))
        WorldType.byName(jsonObject.getAsJsonPrimitive(worldTypeKey).getAsString)
      else
        WorldType.DEFAULT


    private def deserializeBiome(jsonObject: JsonObject) =
      if (jsonObject.has(biomeKey))
        ForgeRegistries.BIOMES.getValue(new ResourceLocation(jsonObject.getAsJsonPrimitive(biomeKey).getAsString))
      else
        Biomes.PLAINS


    private def deserializeBlockState(jsonObject: JsonObject) = {
      val block = Block.getBlockFromName(jsonObject.getAsJsonPrimitive(fillerKey).getAsString)

      if (jsonObject.has(propertiesKey)) {
        var state = block.getDefaultState
        val jsonProps = jsonObject.getAsJsonObject(propertiesKey)
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
        block.getDefaultState
    }

    private def deserializeOffset(jsonObject: JsonObject, key: String) =
      if (jsonObject.has(key)) jsonObject.getAsJsonPrimitive(key).getAsInt else 0


    private def deserializeSeed(jsonObject: JsonObject) =
      if (jsonObject.has(seedOverrideKey))
        Some(jsonObject.getAsJsonPrimitive(seedOverrideKey).getAsLong)
      else
        None

    private def deserializeDimType(jsonObject: JsonObject) =
      DimensionType.byName(jsonObject.getAsJsonPrimitive(dimensionTypeKey).getAsString)
  }
}
