package hohserg.dimensional.layers.lens;

import gloomyfolken.hooklib.api.*;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.relauncher.*;

@SideOnly(Side.CLIENT)
@HookContainer
public class GuiCreateWorldLens {

    @FieldLens
    public static FieldAccessor<GuiCreateWorld, String> worldSeed;
    
    @FieldLens
    public static FieldAccessor<GuiCreateWorld, Integer> selectedIndex;
}
