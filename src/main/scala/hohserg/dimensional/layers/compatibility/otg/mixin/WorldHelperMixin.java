package hohserg.dimensional.layers.compatibility.otg.mixin;

import com.pg85.otg.forge.world.WorldHelper;
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldHelper.class)
public class WorldHelperMixin {
    @Inject(
            method = "getName",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void getName(World world, CallbackInfoReturnable<String> ci) {
        if (world instanceof ProxyWorldServer) {
            ci.setReturnValue(world.provider.getDimensionType().getName());
        }
    }
}
