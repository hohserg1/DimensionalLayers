package hohserg.dimensional.layers.worldgen.proxy.mixin;

import hohserg.dimensional.layers.worldgen.proxy.ShiftedBlockPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockPos.class)
public class BlockPosMixin {
    @Inject(
            method = "add(Lnet/minecraft/util/math/Vec3i;)Lnet/minecraft/util/math/BlockPos;",
            at = @At("HEAD"),
            cancellable = true,
            require = 1,
            allow = 1
    )
    public void add(Vec3i vec, CallbackInfoReturnable<BlockPos> ci) {
        if (vec instanceof ShiftedBlockPos)
            ci.setReturnValue(((ShiftedBlockPos) vec).add(((BlockPos) (Object) this)));
    }

    @Inject(
            method = "subtract(Lnet/minecraft/util/math/Vec3i;)Lnet/minecraft/util/math/BlockPos;",
            at = @At("HEAD"),
            cancellable = true,
            require = 1,
            allow = 1
    )
    public void subtract(Vec3i vec, CallbackInfoReturnable<BlockPos> ci) {
        if (vec instanceof ShiftedBlockPos) {
            BlockPos self = (BlockPos) (Object) this;
            ShiftedBlockPos arg = (ShiftedBlockPos) vec;
            ci.setReturnValue(arg.subtracted(self));
        }
    }
}
