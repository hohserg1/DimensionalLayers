package hohserg.dimensional.layers.preset.sync.mixin;

import hohserg.dimensional.layers.preset.sync.AdditionalPacketData;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public class SetFieldOfPacket {

    @Inject(
            method = "sendPacket",
            at = @At("HEAD")
    )
    public void sendPacket(final Packet<?> packetIn, CallbackInfo ci) {
        if (packetIn instanceof AdditionalPacketData) {
            ((AdditionalPacketData) packetIn).setGeneratorOptions(((NetHandlerPlayServer) ((Object) this)).player.world.getWorldInfo().getGeneratorOptions());
        }
    }


}
