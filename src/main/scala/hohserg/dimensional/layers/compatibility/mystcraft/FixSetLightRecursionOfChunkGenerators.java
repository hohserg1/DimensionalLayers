package hohserg.dimensional.layers.compatibility.mystcraft;

import com.xcompwiz.mystcraft.world.WorldProviderMyst;
import gloomyfolken.hooklib.api.*;
import hohserg.dimensional.layers.worldgen.proxy.ProxyWorldCommon;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;

@HookContainer
public class FixSetLightRecursionOfChunkGenerators {

    public static int recursionCounter = 0;

    @Hook(targetMethod = "setLightFor")
    @OnBegin
    public static ReturnSolve<Void> setLightForStart(Chunk chunk, EnumSkyBlock type, BlockPos pos, int value) {
        recursionCounter++;
        if (recursionCounter > 10)
            return ReturnSolve.yes(null);
        return ReturnSolve.no();
    }

    @Hook(targetMethod = "setLightFor")
    @OnReturn
    public static void setLightForEnd(Chunk chunk, EnumSkyBlock type, BlockPos pos, int value) {
        recursionCounter--;
    }

    @Hook
    @OnBegin
    public static ReturnSolve<Void> verifySpawn(WorldProviderMyst self) {
        if (self.getWorld() instanceof ProxyWorldCommon) {
            return ReturnSolve.no();
        } else {
            self.agedata.setSpawn(BlockPos.ORIGIN);
            return ReturnSolve.yes(null);
        }
    }
}
