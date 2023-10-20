package hohserg.dimensional.layers.gui.mixin;

import net.minecraftforge.fml.client.GuiScrollingList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiScrollingList.class)
public interface AccessorGuiScrollingList {
    @Accessor(value = "scrollDistance", remap = false)
    float getScrollDistance();

    @Accessor(value = "scrollDistance", remap = false)
    void setScrollDistance(float value);

    @Invoker(value = "applyScrollLimits", remap = false)
    void invokeApplyScrollLimits();
}
