package hohserg.dimensional.layers.preset.sync.mixin;

import hohserg.dimensional.layers.preset.sync.AdditionalPacketData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketJoinGame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SPacketJoinGame.class)
public class AddFieldToPacket implements AdditionalPacketData {

    String generatorOptions;

    @Inject(
            method = "writePacketData",
            at = @At("RETURN"),
            require = 1,
            allow = 1
    )
    public void writePacketData(PacketBuffer buf, CallbackInfo ci) {
        buf.writeString(generatorOptions);
    }

    @Inject(
            method = "readPacketData",
            at = @At("RETURN"),
            require = 1
    )
    public void readPacketData(PacketBuffer buf, CallbackInfo ci) {
        generatorOptions = buf.readString(32767);
    }

    @Override
    public String getGeneratorOptions() {
        return generatorOptions;
    }

    @Override
    public void setGeneratorOptions(String v) {
        generatorOptions = v;
    }
}
