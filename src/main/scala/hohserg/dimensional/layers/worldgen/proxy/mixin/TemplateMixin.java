package hohserg.dimensional.layers.worldgen.proxy.mixin;

import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(Template.class)
public class TemplateMixin {
    @Inject(
            method = "addBlocksToWorld(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/structure/template/ITemplateProcessor;Lnet/minecraft/world/gen/structure/template/PlacementSettings;I)V",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/tileentity/TileEntity;readFromNBT(Lnet/minecraft/nbt/NBTTagCompound;)V"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            require = 1,
            allow = 1
    )
    private void injected(World worldIn, BlockPos pos, ITemplateProcessor templateProcessor, PlacementSettings placementIn, int flags,
                          CallbackInfo ci,
                          Block block, StructureBoundingBox structureboundingbox, Iterator var8, Template.BlockInfo template$blockinfo, BlockPos blockpos, Template.BlockInfo template$blockinfo1, Block block1, IBlockState iblockstate, IBlockState iblockstate1, TileEntity tileentity2) {
        if (worldIn instanceof ProxyWorldServer)
            tileentity2.setPos(((ProxyWorldServer) worldIn).bounds().shift(tileentity2.getPos()));
    }
}
