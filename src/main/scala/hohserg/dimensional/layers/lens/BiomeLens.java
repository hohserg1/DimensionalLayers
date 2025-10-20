package hohserg.dimensional.layers.lens;

import gloomyfolken.hooklib.api.*;
import net.minecraft.world.biome.*;

@HookContainer
public class BiomeLens {
    
    @FieldLens
    public static FieldAccessor<Biome,String> biomeName;
}
