package hohserg.dimensional.layers.gens;

import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public interface LayerFiller {

    IBlockState generateBlockState(BlockPos absolutePos);

    void poputale(ICube cube);

    static LayerFiller voidFiller = new LayerFiller() {
        @Override
        public IBlockState generateBlockState(BlockPos absolutePos) {
            return Blocks.AIR.getDefaultState();
        }

        @Override
        public void poputale(ICube cube) {

        }
    };

}
