package hohserg.dimensional.layers.asm.dev.tweaks;

import io.github.opencubicchunks.cubicchunks.core.util.CompatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompatHandler.class)
public class CompatHandlerMixin {

    @Inject(
            method = "getPackageName",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private static void getPackageName(Class<?> clazz, CallbackInfoReturnable<String> ci) {
        if (clazz.getCanonicalName() == null) {
            String name = clazz.getName();
            int dot = name.lastIndexOf('.');
            ci.setReturnValue(dot < 0 ? "" : name.substring(0, dot));
        }
    }
}
