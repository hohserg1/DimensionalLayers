package hohserg.dimensional.layers.compatibility.geographicraft.mixin;

import climateControl.customGenLayer.GenLayerRiverMixWrapper;
import com.Zeno410Utils.Maybe;
import hohserg.dimensional.layers.compatibility.geographicraft.ScalaHelper;
import hohserg.dimensional.layers.data.layer.base.DimensionalGenerator;
import hohserg.dimensional.layers.data.layer.base.Layer;
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerRiverMix;
import net.minecraftforge.common.DimensionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GenLayerRiverMixWrapper.class)
public class GenLayerRiverMixWrapperMixin {
    @Shadow
    private boolean found;
    @Shadow
    private GenLayer redirect;
    @Shadow
    private GenLayer original;
    @Shadow
    private climateControl.DimensionManager dimensionManager;

    @Inject(
            method = "findSelf",
            at = @At("HEAD"),
            remap = false
    )
    public void findSelf(CallbackInfo ci) {
        if (!found) {
            GenLayerRiverMixWrapper self = (GenLayerRiverMixWrapper) ((Object) this);
            for (Integer id : DimensionManager.getIDs()) {
                List<Layer> layers = ScalaHelper.getWorldData(DimensionManager.getWorld(id));
                if (layers != null) {
                    for (Layer layer : layers) {
                        if (layer.generator() instanceof DimensionalGenerator) {
                            ProxyWorldServer proxyWorld = ((DimensionalGenerator) layer.generator()).proxyWorld();
                            if (proxyWorld.getBiomeProvider().genBiomes == self) {
                                Maybe<GenLayerRiverMix> gcLayers = dimensionManager.getGeographicraftGenlayers((WorldServer) (Object) proxyWorld, proxyWorld.provider.getDimension(), original);
                                if (gcLayers.isKnown()) {
                                    redirect = gcLayers.iterator().next();
                                    found = true;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
