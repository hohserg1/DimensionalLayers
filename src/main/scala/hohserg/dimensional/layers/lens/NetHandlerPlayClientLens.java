package hohserg.dimensional.layers.lens;

import gloomyfolken.hooklib.api.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.network.*;

@HookContainer
public class NetHandlerPlayClientLens {

    @FieldLens
    public static FieldAccessor<NetHandlerPlayClient, WorldClient> world;
}
