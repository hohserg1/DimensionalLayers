package hohserg.dimensional.layers

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.{FMLPostInitializationEvent, FMLPreInitializationEvent}

@Mod(modid = Main.modid, name = "DimensionalLayers", version = "3.0", modLanguage = "scala")
object Main {
  final val modid = "dimensional_layers"

  @EventHandler
  def preInit(e: FMLPreInitializationEvent): Unit = {
    new DimensionalLayersWorldType()
  }

  @EventHandler
  def postInit(e: FMLPostInitializationEvent): Unit = {
    DimensionLayersPreset("")
  }

}
