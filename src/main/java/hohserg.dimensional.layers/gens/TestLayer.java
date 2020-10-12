package hohserg.dimensional.layers.gens;

import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TestLayer implements LayerFiller {
    private long seed;
    private int startY;

    public TestLayer(World world, int startY) {
        seed = world.getSeed();
        this.startY = startY;
    }

    @Override
    public IBlockState generateBlockState(BlockPos absolutePos) {
        return Blocks.END_STONE.getDefaultState();
    }

    @Override
    public void poputale(ICube cube) {

    }
}
