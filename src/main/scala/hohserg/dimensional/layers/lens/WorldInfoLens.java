package hohserg.dimensional.layers.lens;

import gloomyfolken.hooklib.api.*;
import net.minecraft.world.storage.*;

@HookContainer
public class WorldInfoLens {
    
    @FieldLens
    public static FieldAccessor<WorldInfo,String> generatorOptions;
}
