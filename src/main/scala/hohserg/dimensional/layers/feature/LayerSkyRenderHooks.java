package hohserg.dimensional.layers.feature;

import gloomyfolken.hooklib.api.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

@HookContainer
public class LayerSkyRenderHooks {

    @Hook
    @OnReturn
    public static Vec3d getSkyColorBody(World world, Entity entityIn, float partialTicks, @ReturnValue Vec3d scalacDoentPreserveAnnotationOnStaticMethod) {
        return LayerSkyRender.getSkyColorBody(world, entityIn, partialTicks, scalacDoentPreserveAnnotationOnStaticMethod);
    }
}
