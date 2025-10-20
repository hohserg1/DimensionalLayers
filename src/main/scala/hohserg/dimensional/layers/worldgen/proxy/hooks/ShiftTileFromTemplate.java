package hohserg.dimensional.layers.worldgen.proxy.hooks;

import gloomyfolken.hooklib.api.*;
import hohserg.dimensional.layers.worldgen.proxy.server.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.gen.structure.template.*;

@HookContainer
public class ShiftTileFromTemplate {

    @Hook
    @OnMethodCall("readFromNBT")
    public void addBlocksToWorld(World worldIn, BlockPos pos, ITemplateProcessor templateProcessor, PlacementSettings placementIn, int flags,
                                 @LocalVariable(id = 15) TileEntity tileentity2) {
        if (worldIn instanceof ProxyWorldServer proxy) {
            tileentity2.setPos(proxy.bounds().shift(tileentity2.getPos()));
        }
    }
}
