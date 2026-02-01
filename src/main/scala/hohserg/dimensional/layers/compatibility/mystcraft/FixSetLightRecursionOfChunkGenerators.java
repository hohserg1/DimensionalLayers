package hohserg.dimensional.layers.compatibility.mystcraft;

import gloomyfolken.hooklib.api.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;

@HookContainer
public class FixSetLightRecursionOfChunkGenerators {

    @FieldLens(isMandatory = false)
    public static FieldAccessor<Chunk, Boolean> isColumn;

    @Hook
    @OnBegin
    public static ReturnSolve<Void> setLightFor(Chunk chunk, EnumSkyBlock type, BlockPos pos, int value) {
        //if (isColumn.get(chunk))
       //     return ReturnSolve.yes(null);
      // else
            return ReturnSolve.no();
    }
}
