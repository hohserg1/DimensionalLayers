package hohserg.dimension.layers

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.{FMLPostInitializationEvent, FMLPreInitializationEvent}

@Mod(modid = Main.modid, name = "DimensionLayers", version = "3.0", modLanguage = "scala")
object Main {
  final val modid = "dimension_layers"

  @EventHandler
  def preInit(e: FMLPreInitializationEvent): Unit = {
    new DimensionalLayersWorldType()
  }

  @EventHandler
  def postInit(e: FMLPostInitializationEvent): Unit = {
    DimensionLayersPreset("")
  }

}
