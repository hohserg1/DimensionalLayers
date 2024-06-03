package hohserg.dimensional.layers.preset.sync.mixin;

import hohserg.dimensional.layers.preset.sync.AdditionalPacketData;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerList.class)
public class SetFieldOfPacket {

    @Redirect(
            method = "initializeConnectionToPlayer",
            at = @At(value = "INVOKE", target = "sendPacket", ordinal = 0)
    )
    public void initializeConnectionToPlayer(NetHandlerPlayServer nethandlerplayserver, final Packet<?> packetIn) {
        ((AdditionalPacketData) packetIn).setGeneratorOptions(nethandlerplayserver.player.world.getWorldInfo().getGeneratorOptions());
        nethandlerplayserver.sendPacket(packetIn);
    }
}
