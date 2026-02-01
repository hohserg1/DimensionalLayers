package hohserg.dimensional.layers.compatibility.cleanroom;

import gloomyfolken.hooklib.api.*;
import io.github.opencubicchunks.cubicchunks.core.server.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.player.*;

@HookContainer
public class FixFastutilsUpdate {

    @FieldLens(isMandatory = false)
    public static FieldAccessor<CubeWatcher, ObjectArrayList<EntityPlayerMP>> playersToAdd;

    @Hook(isMandatory = false)
    @OnMethodCall(value = "rem", shift = Shift.INSTEAD)
    public static boolean removeScheduledAddPlayer(CubeWatcher self, EntityPlayerMP player) {
        return playersToAdd.get(self).remove(player);
    }
}
