package hohserg.dimensional.layers.lens;

import gloomyfolken.hooklib.api.*;
import net.minecraft.world.*;

@HookContainer
public class DimensionTypeLens {
    
    @FieldLens
    public static FieldAccessor<DimensionType,Class<? extends WorldProvider>> clazz;
    
    @FieldLens
    public static FieldAccessor<DimensionType,String> suffix;
}
