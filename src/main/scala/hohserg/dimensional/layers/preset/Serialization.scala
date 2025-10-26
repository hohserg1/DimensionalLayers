package hohserg.dimensional.layers.preset

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.command.CommandBase
import net.minecraft.init.{Biomes, Blocks, MobEffects}
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation
import net.minecraft.world.biome.Biome
import net.minecraft.world.{DimensionType, WorldType}
import net.minecraftforge.fml.common.registry.ForgeRegistries

import scala.jdk.CollectionConverters.*

object Serialization {

  def toJson(preset: DimensionalLayersPreset): String = writeToString(preset)

  def fromJson(json: String): DimensionalLayersPreset = readFromString(json)

  given JsonValueCodec[Biome] = stringMapper(_.getRegistryName.toString, x => ForgeRegistries.BIOMES.getValue(new ResourceLocation(x)), Biomes.PLAINS)

  given JsonValueCodec[DimensionType] = stringMapper(_.getName, DimensionType.byName, DimensionType.OVERWORLD)

  given JsonValueCodec[Potion] = stringMapper(_.getRegistryName.toString, x => ForgeRegistries.POTIONS.getValue(new ResourceLocation(x)), MobEffects.NIGHT_VISION)

  given worldTypeCodec: JsonValueCodec[WorldType] = stringMapper(_.getName, WorldType.byName, WorldType.DEFAULT)

  given JsonValueCodec[WorldType & ICubicWorldType] = new JsonValueCodec[WorldType & ICubicWorldType] {
    override def decodeValue(in: JsonReader, default: WorldType & ICubicWorldType): WorldType & ICubicWorldType =
      worldTypeCodec.decodeValue(in, default) match {
        case cubic: ICubicWorldType => cubic
        case _ => default
      }

    override def encodeValue(x: WorldType & ICubicWorldType, out: JsonWriter): Unit =
      worldTypeCodec.encodeValue(x, out)

    override def nullValue: WorldType & ICubicWorldType =
      CubicWorldTypeHelper.possibleWorldTypes.headOption
                          .getOrElse(throw new IllegalStateException("there is no cubic world types. need to add CubicWorldGen or smth"))
  }

  given JsonValueCodec[IBlockState] = stringMapper(
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
    },
    Blocks.AIR.getDefaultState
  )

  def getPropValueName[A <: Comparable[A]](prop: IProperty[A], v: Comparable[?]): String = {
    prop.getName(v.asInstanceOf[A])
  }

  given JsonValueCodec[DimensionalLayersPreset] = JsonCodecMaker.make

  def stringMapper[A](to: A => String, from: String => A, default: A): JsonValueCodec[A] = new JsonValueCodec[A] {
    override def decodeValue(in: JsonReader, default: A): A = from(in.readString(to(default)))

    override def encodeValue(x: A, out: JsonWriter): Unit = out.writeVal(to(x))

    override def nullValue: A = default
  }
}
