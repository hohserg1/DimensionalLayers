package hohserg.dimensional.layers;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Main.modid, name = "DimensionalLayers")
public class Main {
    public static final String modid = "dimensional_layers";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        new DimensionLayersWorldType();
    }
}
