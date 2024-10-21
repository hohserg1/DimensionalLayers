package hohserg.dimensional.layers.preset.sync;

import gloomyfolken.hooklib.api.FieldAccessor;
import gloomyfolken.hooklib.api.FieldLens;
import net.minecraft.network.play.server.SPacketJoinGame;

//@HookContainer
public class AddFieldToPacket {

    @FieldLens(createField = true)
    public static FieldAccessor<SPacketJoinGame, String> generatorOptions;
}
