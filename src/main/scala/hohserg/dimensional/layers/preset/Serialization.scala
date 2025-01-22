package hohserg.dimensional.layers.preset

import com.google.gson._
import com.google.gson.stream.MalformedJsonException
import hohserg.dimensional.layers.preset.spec._
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.command.CommandBase
import net.minecraft.init.Biomes
import net.minecraft.util.ResourceLocation
import net.minecraft.world.biome.Biome
import net.minecraft.world.{DimensionType, WorldType}
import net.minecraftforge.fml.common.registry.ForgeRegistries
import org.apache.commons.lang3.ClassUtils

import java.lang.reflect.{ParameterizedType, Type}
import scala.collection.JavaConverters._
import scala.collection.immutable.ListMap
import scala.reflect.ClassTag
import scala.reflect.runtime._
import scala.reflect.runtime.universe._

object Serialization {
  val gson: Gson = {
    val builder = new GsonBuilder
    builder
      .registerSerializer(optionSerializer[Long])
      .registerSerializer(optionSerializer[String])
      .registerSerializer(mapSerializer)
      .registerMapper[DimensionType, String](_.getName, DimensionType.byName)
      .registerMapper[WorldType, String](_.getName, WorldType.byName)
      .registerSerializer(blockStateSerializer)
      .registerMapper[Biome, String](biome => biome.getRegistryName.toString, x => ForgeRegistries.BIOMES.getValue(new ResourceLocation(x)))
      .registerMapper[List[LayerSpec], java.util.List[LayerSpec]](_.asJava, x => x.asScala.toList, hierarchic = false)
      .registerSerializer(presetSerializer, hierarchic = false)
      .registerSerializer(layerSpecSerializer, hierarchic = false)
      .registerSerializer(dimensionLayerSpecSerializer, hierarchic = false)
      .registerSerializer(solidLayerSpecSerializer, hierarchic = false)
      .registerSerializer(cubicWorldTypeLayerSpecSerializer, hierarchic = false)
      .registerSerializer(openTerrainGeneratorLayerSpecSpecSerializer, hierarchic = false)

    builder.create()
  }

  def openTerrainGeneratorLayerSpecSpecSerializer: JsonSerializer[OpenTerrainGeneratorLayerSpec] with JsonDeserializer[OpenTerrainGeneratorLayerSpec] =
    new JsonSerializer[OpenTerrainGeneratorLayerSpec] with JsonDeserializer[OpenTerrainGeneratorLayerSpec] {
      override def serialize(src: OpenTerrainGeneratorLayerSpec, typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
        context.serialize(ListMap(
          "presetName" -> src.presetName,
          putOrElse("configYml", src.configYml, None)
        ))

      override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): OpenTerrainGeneratorLayerSpec = {
        val jsonObject = json.getAsJsonObject
        OpenTerrainGeneratorLayerSpec(
          jsonObject.get("presetName").getAsString,
          getOrElse(jsonObject, "configYml", None, context)
        )
      }
    }

  def cubicWorldTypeLayerSpecSerializer: JsonSerializer[CubicWorldTypeLayerSpec] with JsonDeserializer[CubicWorldTypeLayerSpec] =
    new JsonSerializer[CubicWorldTypeLayerSpec] with JsonDeserializer[CubicWorldTypeLayerSpec] {
      override def serialize(src: CubicWorldTypeLayerSpec, typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
        context.serialize(ListMap(
          "cubicWorldType" -> src.cubicWorldType,
          putOrElse("worldTypePreset", src.worldTypePreset, ""),
          putOrElse("dimensionType1", src.dimensionType1, DimensionType.OVERWORLD),
          putOrElse("minCubeY", src.minCubeY, 0),
          putOrElse("maxCubeY", src.maxCubeY, 32),
          putOrElse("seedOverride", src.seedOverride, None)
        ))

      override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): CubicWorldTypeLayerSpec = {
        val jsonObject = json.getAsJsonObject
        CubicWorldTypeLayerSpec(
          context.deserialize(jsonObject.get("cubicWorldType"), classOf[WorldType]),
          getOrElse(jsonObject, "worldTypePreset", "", context),
          getOrElse(jsonObject, "dimensionType1", DimensionType.OVERWORLD, context),
          getOrElse(jsonObject, "minCubeY", 0, context),
          getOrElse(jsonObject, "maxCubeY", 32, context),
          getOrElse(jsonObject, "seedOverride", None, context)
        )
      }
    }

  def solidLayerSpecSerializer: JsonSerializer[SolidLayerSpec] with JsonDeserializer[SolidLayerSpec] =
    new JsonSerializer[SolidLayerSpec] with JsonDeserializer[SolidLayerSpec] {
      override def serialize(src: SolidLayerSpec, typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
        context.serialize(ListMap(
          "filler" -> src.filler,
          "height" -> src.height,
          putOrElse("biome", src.biome, Biomes.PLAINS)
        ))

      override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SolidLayerSpec = {
        val jsonObject = json.getAsJsonObject
        SolidLayerSpec(
          context.deserialize(jsonObject.get("filler"), classOf[IBlockState]),
          jsonObject.getAsJsonPrimitive("height").getAsInt,
          getOrElse(jsonObject, "biome", Biomes.PLAINS, context)
        )
      }
    }

  def dimensionLayerSpecSerializer: JsonSerializer[DimensionLayerSpec] with JsonDeserializer[DimensionLayerSpec] =
    new JsonSerializer[DimensionLayerSpec] with JsonDeserializer[DimensionLayerSpec] {
      override def serialize(src: DimensionLayerSpec, typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
        context.serialize(ListMap(
          "dimensionType" -> src.dimensionType,
          putOrElse("seedOverride", src.seedOverride, None),
          putOrElse("topOffset", src.topOffset, 0),
          putOrElse("bottomOffset", src.bottomOffset, 0),
          putOrElse("worldType", src.worldType, WorldType.DEFAULT),
          putOrElse("worldTypePreset", src.worldTypePreset, "")
        ))


      override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DimensionLayerSpec = {
        val jsonObject = json.getAsJsonObject
        DimensionLayerSpec(
          context.deserialize(jsonObject.get("dimensionType"), classOf[DimensionType]),
          getOrElse(jsonObject, "seedOverride", None, context),
          getOrElse(jsonObject, "topOffset", 0, context),
          getOrElse(jsonObject, "bottomOffset", 0, context),
          getOrElse(jsonObject, "worldType", WorldType.DEFAULT, context),
          getOrElse(jsonObject, "worldTypePreset", "", context)
        )
      }
    }

  def putOrElse[A](name: String, v: A, default: A): (String, A) =
    if (v == default)
      "" -> default
    else
      name -> v

  def getOrElse[A: TypeTag](jsonObject: JsonObject, name: String, default: A, context: JsonDeserializationContext): A = {
    val requiredType = parameterizedType(implicitly[TypeTag[A]].tpe)
    if (jsonObject.has(name)) context.deserialize(jsonObject.get(name), requiredType) else default
  }

  def mapSerializer: JsonSerializer[Map[String, Object]] with JsonDeserializer[Map[String, Object]] =
    mapperSerializer[Map[String, Object], java.util.Map[String, Object]](_.-("").asJava, _.asScala.toMap)

  def layerSpecSerializer: JsonSerializer[LayerSpec] with JsonDeserializer[LayerSpec] =
    new JsonSerializer[LayerSpec] with JsonDeserializer[LayerSpec] {
      override def serialize(src: LayerSpec, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
        context.serialize(src, src.getClass)
      }

      override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LayerSpec = {
        val jsonObject = json.getAsJsonObject

        if (jsonObject.has("dimensionType"))
          context.deserialize(jsonObject, classOf[DimensionLayerSpec])

        else if (jsonObject.has("filler"))
          context.deserialize(jsonObject, classOf[SolidLayerSpec])

        else if (jsonObject.has("cubicWorldType"))
          context.deserialize(jsonObject, classOf[CubicWorldTypeLayerSpec])

        else if (jsonObject.has("presetName"))
          context.deserialize(jsonObject, classOf[OpenTerrainGeneratorLayerSpec])

        else
          throw new MalformedJsonException("unknown layer type")
      }
    }

  def presetSerializer: JsonSerializer[DimensionalLayersPreset] with JsonDeserializer[DimensionalLayersPreset] =
    new JsonSerializer[DimensionalLayersPreset] with JsonDeserializer[DimensionalLayersPreset] {
      override def serialize(src: DimensionalLayersPreset, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
        val r = new JsonObject
        val overworldLayer = new JsonObject
        overworldLayer.add("layers", context.serialize(src.layers))
        overworldLayer.addProperty("startCubeY", src.startCubeY)
        r.add("0", overworldLayer)
        r
      }

      override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DimensionalLayersPreset = {
        val o = json.getAsJsonObject
        val overworldLayer = o.getAsJsonObject("0")
        DimensionalLayersPreset(
          context.deserialize(overworldLayer.getAsJsonArray("layers"), parameterizedType(implicitly[TypeTag[List[LayerSpec]]].tpe)),
          overworldLayer.get("startCubeY").getAsInt
        )
      }
    }

  implicit class RichGsonBuilder(val gb: GsonBuilder) extends AnyVal {
    def registerMapper[A: ClassTag, B: TypeTag](to: A => B, from: B => A, hierarchic: Boolean = true): GsonBuilder = {
      registerSerializer(mapperSerializer[A, B](to, from))
      gb
    }

    def registerSerializer[A: ClassTag](s: JsonSerializer[A] with JsonDeserializer[A], hierarchic: Boolean = true): GsonBuilder = {
      val register: (Class[_], Any) => GsonBuilder = if (hierarchic) gb.registerTypeHierarchyAdapter else gb.registerTypeAdapter
      register(implicitly[ClassTag[A]].runtimeClass, s)
      gb
    }
  }

  def blockStateSerializer: JsonSerializer[IBlockState] with JsonDeserializer[IBlockState] =
    mapperSerializer[IBlockState, String](
      { blockstate =>
        blockstate.getBlock.getRegistryName.toString + (
          if (blockstate == blockstate.getBlock.getDefaultState)
            ""
          else
            blockstate.getProperties.asScala.map { case (prop, v) => prop.getName + ":" + getPropValueName(prop, v) }.mkString("[", ",", "]")
          )
      },
      { str =>
        val propStart = str.indexOf('[')
        val block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(if (propStart == -1) str else str.substring(0, propStart)))
        if (propStart == -1)
          block.getDefaultState
        else {
          val props = str.substring(propStart + 1, str.length - 1)
          CommandBase.convertArgToBlockState(block, props)
        }
      }
    )

  def getPropValueName[A <: Comparable[A]](prop: IProperty[A], v: Comparable[_]): String =
    prop.getName(v.asInstanceOf[A])

  def enumSerializer[A <: Enum[A] : ClassTag]: JsonSerializer[A] with JsonDeserializer[A] = {
    val cl = implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]]
    mapperSerializer[A, String](_.name, Enum.valueOf(cl, _))
  }

  case class ParameterizedTypeImpl(getRawType: Type, getActualTypeArguments: Array[Type]) extends ParameterizedType {
    override def getOwnerType: Type = null
  }

  def parameterizedType[A](t: scala.reflect.runtime.universe.Type): Type = {
    val tt = t match {
      case refinedType: RefinedType => refinedType.parents.head
      case _ => t
    }
    val typeConstructor = currentMirror.runtimeClass(tt)

    val innerTypes = t.typeArgs
      .map(parameterizedType)
      .map {
        case cl: Class[_] if cl.isPrimitive => ClassUtils.primitiveToWrapper(cl)
        case other => other
      }
      .toArray

    if (innerTypes.isEmpty)
      typeConstructor
    else
      ParameterizedTypeImpl(typeConstructor, innerTypes)
  }

  def optionSerializer[A: TypeTag]: JsonSerializer[Option[A]] with JsonDeserializer[Option[A]] = {
    val elementType = parameterizedType(implicitly[TypeTag[A]].tpe)

    new JsonSerializer[Option[A]] with JsonDeserializer[Option[A]] {
      override def serialize(src: Option[A], typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
        src match {
          case Some(x) => context.serialize(x, elementType)
          case None => new JsonObject
        }

      override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Option[A] = {
        if (json.isJsonObject && json.getAsJsonObject.entrySet().isEmpty)
          None
        else {
          Some(context.deserialize(json, elementType))
        }
      }
    }
  }

  def mapperSerializer[A, B: TypeTag](to: A => B, from: B => A): JsonSerializer[A] with JsonDeserializer[A] = {
    val b_type = parameterizedType(implicitly[TypeTag[B]].tpe)

    new JsonSerializer[A] with JsonDeserializer[A] {
      override def serialize(src: A, typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
        context.serialize(to(src), b_type)

      override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): A =
        from(context.deserialize(json, b_type))
    }
  }
}
