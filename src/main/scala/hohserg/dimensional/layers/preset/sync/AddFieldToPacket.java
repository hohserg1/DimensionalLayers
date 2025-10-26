package hohserg.dimensional.layers.preset.sync;

import gloomyfolken.hooklib.api.*;
import net.minecraft.network.play.server.*;

@HookContainer
public class AddFieldToPacket {

    @FieldLens(createField = true)
    public static FieldAccessor<SPacketJoinGame, Boolean> isDimensionalLayersWorldType;
    
    @FieldLens(createField = true)
    public static FieldAccessor<SPacketJoinGame, String> generatorOptions;
}
