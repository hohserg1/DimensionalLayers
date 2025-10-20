package hohserg.dimensional.layers.lens;

import gloomyfolken.hooklib.api.*;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;

@HookContainer
public class DimensionManagerLens {

    @FieldLens
    public static FieldAccessor<DimensionManager, Int2ObjectMap<WorldServer>> worlds;
}
