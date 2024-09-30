package hohserg.dimensional.layers.preset

import com.google.gson._
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.command.CommandBase
import net.minecraft.util.ResourceLocation
import net.minecraft.world.biome.Biome
import net.minecraft.world.{DimensionType, WorldType}
import net.minecraftforge.fml.common.registry.ForgeRegistries

import java.lang.reflect.{ParameterizedType, Type}
import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import scala.reflect.runtime._
import scala.reflect.runtime.universe._

object Serialization {
  val gson: Gson = {
    val builder = new GsonBuilder
    builder
      .registerSerializer(optionSerializer[Long])
      .registerMapper[DimensionType, String](_.getName, DimensionType.byName)
      .registerMapper[WorldType, String](_.getName, WorldType.byName)
      .registerSerializer(blockStateSerializer)
      .registerMapper[Biome, String](biome => biome.getRegistryName.toString, x => ForgeRegistries.BIOMES.getValue(new ResourceLocation(x)))
      .registerMapper[List[LayerSpec], java.util.List[LayerSpec]](_.asJava, x => x.asScala.toList, hierarchic = false)
      .registerTypeAdapter(classOf[DimensionalLayersPreset], new ProductSerializer(implicitly[TypeTag[DimensionalLayersPreset]].tpe.typeSymbol.asClass))

    val layerSpecSymbol = implicitly[TypeTag[LayerSpec]].tpe.typeSymbol.asClass
    val caseClasses = getAllSubCaseClasses(layerSpecSymbol)
    val traits = getAllTraits(layerSpecSymbol)

    caseClasses.foreach { cl => builder.registerTypeAdapter(currentMirror.runtimeClass(cl), new ProductSerializer(cl)) }
    traits.foreach { cl => builder.registerTypeAdapter(currentMirror.runtimeClass(cl), new CoproductSerializer(cl)) }

    builder.create()
  }

  class ProductSerializer(cl: ClassSymbol) extends JsonSerializer[Product] with JsonDeserializer[Product] {
    val companionType = cl.companion.asModule.typeSignature

    val companionMirror = currentMirror.reflect(currentMirror.reflectModule(cl.companion.asModule).instance)

    val applyMethod = companionType.member(TermName("apply")).asTerm.alternatives.head.asMethod

    val applyMirror = companionMirror.reflectMethod(applyMethod)

    val fields: IndexedSeq[(String, Type, Option[Any])] = applyMethod.paramLists.head
      .collect { case m: TermSymbol => m }
      .zipWithIndex
      .map { case (m, i) =>
        val paramName = m.name.toString
        val default: Option[Any] =
          if (m.isParamWithDefault) {
            val defaultGetterName = "apply$default$" + (i + 1)
            val defaultGetter = companionType.member(TermName(defaultGetterName))
            if (defaultGetter != NoSymbol) {
              Some(companionMirror.reflectMethod(defaultGetter.asMethod).apply())

            } else
              throw new RuntimeException("bruh, missing default getter for argument \"" + paramName + "\", required \"" + defaultGetterName + "\"")

          } else {
            None
          }

        (paramName, parameterizedType(m.typeSignature), default)
      }.toIndexedSeq

    override def serialize(src: Product, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      val r = new JsonObject
      for {
        i <- 0 until src.productArity
        v = src.productElement(i)
        (name, argType, default) = fields(i)
        if !default.contains(v)
      } {
        r.add(name, context.serialize(v, argType))
      }
      r
    }

    override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Product = {
      val o = json.getAsJsonObject

      applyMirror.apply((
        for {
          (name, argType, default) <- fields
        } yield {
          if (o.has(name))
            context.deserialize(o.get(name), argType)
          else
            default.getOrElse(throw new JsonParseException("missing mandatory value: " + name))
        }
        ): _*).asInstanceOf[Product]
    }
  }

  class CoproductSerializer(cl: ClassSymbol) extends JsonSerializer[Any] with JsonDeserializer[Any] {
    val possibilities: Set[ClassSymbol] = getDirectSubClasses(cl).filter(_.isCaseClass)

    val classesForField: Map[String, Set[universe.ClassSymbol]] = possibilities.flatMap { s =>
      s.toType.members
        .collect(onlyMandatoryCaseClassAccessor)
        .map(_.name.toString)
        .map(_ -> s)
    }.groupBy { case (fieldName, _) => fieldName }
      .mapValues(_.map { case (_, classSymbol) => classSymbol })

    val uniqueFieldForClass: Seq[(String, Class[_])] = classesForField
      .filter(_._2.size == 1)
      .mapValues(_.head)
      .mapValues(currentMirror.runtimeClass)
      .toSeq

    assert(uniqueFieldForClass.map { case (_, cl) => cl }.toSet.size == possibilities.size, "bruh, we have overlapsed case classes in sealed trait " + cl.name)

    def onlyMandatoryCaseClassAccessor: PartialFunction[Symbol, MethodSymbol] = {
      case m: MethodSymbol if m.isCaseAccessor && !m.isParamWithDefault => m
    }

    override def serialize(src: Any, typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
      context.serialize(src, typeOfSrc)

    override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Any = {
      val o = json.getAsJsonObject
      uniqueFieldForClass
        .find { case (fieldName, _) => o.has(fieldName) }
        .map { case (_, cl) => context.deserialize[Any](json, cl) }
        .getOrElse(throw new JsonParseException("unknown structure in json: " + o))
    }
  }

  def getAllSubCaseClasses(s: ClassSymbol): Set[ClassSymbol] = {
    s match {
      case t if t.isTrait => getDirectSubClasses(t).flatMap(getAllSubCaseClasses)
      case c if c.isCaseClass => Set(c)
    }
  }

  def getAllTraits(s: ClassSymbol): Set[ClassSymbol] = {
    s match {
      case t if t.isTrait => getDirectSubClasses(t).flatMap(getAllTraits) + t
      case c if c.isCaseClass => Set()
    }
  }

  def getDirectSubClasses(t: ClassSymbol): Set[ClassSymbol] = {
    t.knownDirectSubclasses.filter(_.isClass).map(_.asClass)
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

    val innerTypes = t.typeArgs.map(parameterizedType).toArray

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
