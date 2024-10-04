package hohserg.dimensional.layers.asm.dev.tweaks;

import com.Zeno410Utils.Accessor;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Accessor.class)
public class GeographicraftAccessorFix {

    @Shadow
    String fieldName;


    @Inject(
            method = "setField(Ljava/lang/Class;)V",
            at = @At("HEAD")
    )
    public void deobfFieldName(Class classObject, CallbackInfo ci) {
        String internalClassName = FMLDeobfuscatingRemapper.INSTANCE.unmap(classObject.getName().replace('.', '/'));
        fieldName = FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(internalClassName, fieldName, null);
    }
}
