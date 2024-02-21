package hohserg.dimensional.layers.compatibility.event.mixin;

import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.ASMEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ASMEventHandler.class)
public interface AccessorASMEventHandler {
    @Accessor(value = "owner", remap = false)
    ModContainer getOwner();
}
