package hohserg.dimensional.layers.lens;

import gloomyfolken.hooklib.api.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.renderer.*;

@HookContainer
public class RenderGlobalLens {
    
    @FieldLens
    public static FieldAccessor<RenderGlobal, WorldClient> world;
}
