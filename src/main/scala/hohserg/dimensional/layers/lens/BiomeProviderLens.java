package hohserg.dimensional.layers.lens;

import gloomyfolken.hooklib.api.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.layer.*;

@HookContainer
public class BiomeProviderLens {
    
    @FieldLens
    public static FieldAccessor<BiomeProvider, GenLayer> genBiomes;
}
