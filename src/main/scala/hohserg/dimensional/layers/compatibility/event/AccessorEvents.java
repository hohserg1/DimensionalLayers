package hohserg.dimensional.layers.compatibility.event;

import gloomyfolken.hooklib.api.FieldAccessor;
import gloomyfolken.hooklib.api.FieldLens;
import gloomyfolken.hooklib.api.HookContainer;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.ASMEventHandler;
import net.minecraftforge.fml.common.eventhandler.EventBus;

@HookContainer
public class AccessorEvents {

    @FieldLens
    public static FieldAccessor<ASMEventHandler, ModContainer> owner;

    @FieldLens
    public static FieldAccessor<EventBus, Integer> busID;

}
