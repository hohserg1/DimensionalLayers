package hohserg.dimensional.layers.replace.teleport.to.dimension.mixin;

import hohserg.dimensional.layers.replace.teleport.to.dimension.ILastUsedTeleporter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.ITeleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerMP.class)
public class CatchPlayerTeleporter implements ILastUsedTeleporter {

    private ITeleporter lastUsedTeleporter;

    @Inject(
            method = "changeDimension(ILnet/minecraftforge/common/util/ITeleporter;)Lnet/minecraft/entity/Entity;",
            at = @At("HEAD")
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