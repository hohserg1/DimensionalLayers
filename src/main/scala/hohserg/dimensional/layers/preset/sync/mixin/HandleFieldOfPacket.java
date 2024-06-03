package hohserg.dimensional.layers.preset.sync.mixin;

import hohserg.dimensional.layers.preset.sync.AdditionalPacketData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NetHandlerPlayClient.class)
public class HandleFieldOfPacket {

    @Inject(
            method = "handleJoinGame",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void handleJoinGame(SPacketJoinGame packetIn, CallbackInfo ci) {
        WorldInfo worldInfo = Minecraft.getMinecraft().world.getWorldInfo();
        WorldSettings worldSettings = new WorldSettings(worldInfo);
        worldSettings.setGeneratorOptions(((AdditionalPacketData) packetIn).getGeneratorOptions());
        worldInfo.populateFromWorldSettings(worldSettings);
        System.out.println("generatorOptions " + worldSettings.getGeneratorOptions());
    }
}
