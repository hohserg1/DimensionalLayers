package hohserg.dimensional.layers.replace.teleport.to.dimension.mixin;

import hohserg.dimensional.layers.replace.teleport.to.dimension.ILastUsedTeleporter;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.util.ITeleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class CatchEntityTeleporter implements ILastUsedTeleporter {

    private ITeleporter lastUsedTeleporter;

    @Inject(
            method = "changeDimension(ILnet/minecraftforge/common/util/ITeleporter;)Lnet/minecraft/entity/Entity;",
            at = @At("HEAD"),
            require = 1,
            allow = 1
    )
    public void changeDimension(int dimensionIn, ITeleporter teleporter,
                                CallbackInfoReturnable<Entity> ci) {
        lastUsedTeleporter = teleporter;
    }

    @Override
    public ITeleporter getLastUsedTeleporter() {
        return lastUsedTeleporter;
    }
}
