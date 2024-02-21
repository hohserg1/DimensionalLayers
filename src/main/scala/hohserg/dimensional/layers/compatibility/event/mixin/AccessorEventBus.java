package hohserg.dimensional.layers.compatibility.event.mixin;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EventBus.class)
public interface AccessorEventBus {
    @Accessor(value = "busID", remap = false)
    int getBusID();
}
