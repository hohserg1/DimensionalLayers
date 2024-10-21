package hohserg.dimensional.layers.gui;

import gloomyfolken.hooklib.api.FieldAccessor;
import gloomyfolken.hooklib.api.FieldLens;
import gloomyfolken.hooklib.api.HookContainer;
import gloomyfolken.hooklib.api.MethodLens;
import net.minecraftforge.fml.client.GuiScrollingList;

@HookContainer
public class AccessorGuiScrollingList {

    @FieldLens
    public static FieldAccessor<GuiScrollingList, Float> scrollDistance;

    @MethodLens
    public static void applyScrollLimits(GuiScrollingList guiScrollingList) {

    }
}
