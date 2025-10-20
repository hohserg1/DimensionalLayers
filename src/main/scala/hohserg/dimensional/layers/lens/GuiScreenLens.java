package hohserg.dimensional.layers.lens;

import gloomyfolken.hooklib.api.*;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.relauncher.*;

import java.net.*;

@SideOnly(Side.CLIENT)
@HookContainer
public class GuiScreenLens {

    @MethodLens
    public static void openWebLink(GuiScreen guiScreen, URI url) {

    }
}
