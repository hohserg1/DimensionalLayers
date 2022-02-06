package hohserg.dimension.layers

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(modid = "dimension_layers",name = "DimensionLayers",version = "1.0")
object Main {
  @EventHandler
  def preInit(e: FMLPreInitializationEvent): Unit = {
    new DimensionalLayersWorldType()
  }

}
