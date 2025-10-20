package hohserg.dimensional.layers.lens;

import gloomyfolken.hooklib.api.*;
import net.minecraft.entity.player.*;

@HookContainer
public class EntityPlayerMPLens {
    
    @FieldLens
    public static FieldAccessor<EntityPlayerMP, Boolean> invulnerableDimensionChange;
}
