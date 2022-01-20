package hohserg.dimensional.layers

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(modid = "dimensional_layers", name = "DimensionalLayers", version = "2.0", modLanguage = "scala")
object Main {
  @EventHandler
  def preInit(e: FMLPreInitializationEvent): Unit = {
    new DimensionalLayersWorldType()

  }

}
