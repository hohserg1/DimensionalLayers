package hohserg.dimensional.layers.lens;

import gloomyfolken.hooklib.api.*;
import net.minecraft.entity.*;

@HookContainer
public class EntityLens {

    @FieldLens
    public static FieldAccessor<Entity, Boolean> inPortal;

    @FieldLens
    public static FieldAccessor<Entity, Integer> portalCounter;

}
