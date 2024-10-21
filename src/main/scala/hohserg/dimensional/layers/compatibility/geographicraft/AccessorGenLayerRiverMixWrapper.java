package hohserg.dimensional.layers.compatibility.geographicraft;

import climateControl.customGenLayer.GenLayerRiverMixWrapper;
import gloomyfolken.hooklib.api.FieldAccessor;
import gloomyfolken.hooklib.api.FieldLens;
import gloomyfolken.hooklib.api.HookContainer;
import net.minecraft.world.gen.layer.GenLayer;

@HookContainer
public class AccessorGenLayerRiverMixWrapper {

    @FieldLens
    public static FieldAccessor<GenLayerRiverMixWrapper, Boolean> found;

    @FieldLens
    public static FieldAccessor<GenLayerRiverMixWrapper, GenLayer> redirect;

    @FieldLens
    public static FieldAccessor<GenLayerRiverMixWrapper, GenLayer> original;

    @FieldLens
    public static FieldAccessor<GenLayerRiverMixWrapper, climateControl.DimensionManager> dimensionManager;
}
