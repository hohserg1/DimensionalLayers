package hohserg.dimensional.layers;

import com.google.common.collect.ImmutableList;
import hohserg.dimensional.layers.config.LayersConfig;
import hohserg.dimensional.layers.gens.LayerFiller;
import io.github.opencubicchunks.cubicchunks.api.util.Box;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import io.github.opencubicchunks.cubicchunks.api.worldgen.CubePrimer;
import io.github.opencubicchunks.cubicchunks.api.worldgen.ICubeGenerator;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;
import java.util.List;

import static hohserg.dimensional.layers.config.LayersConfig.getLayerFiller;

public class DimensionLayersGenerator implements ICubeGenerator {
    private final World world;

    public DimensionLayersGenerator(World world) {
        this.world = world;
        LayersConfig.init(world);
    }

    @Override
    public CubePrimer generateCube(int cubeX, int cubeY, int cubeZ) {
        CubePrimer r = new CubePrimer();

        LayerFiller layerFiller = getLayerFiller(cubeY);

        BlockPos startOfCube = new BlockPos(cubeX << 4, cubeY << 4, cubeZ << 4);
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++)
                for (int z = 0; z < 16; z++)
                    r.setBlockState(x, y, z, layerFiller.generateBlockState(startOfCube.add(x, y, z)));

        return r;
    }

    @Override
    public void generateColumn(Chunk column) {

    }

    @Override
    public void populate(ICube cube) {
        getLayerFiller(cube.getY()).poputale(cube);
    }

    @Override
    public Box getFullPopulationRequirements(ICube cube) {
        return NO_REQUIREMENT;
    }

    @Override
    public Box getPopulationPregenerationRequirements(ICube cube) {
        return NO_REQUIREMENT;
    }

    @Override
    public void recreateStructures(ICube cube) {

    }

    @Override
    public void recreateStructures(Chunk column) {

    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType type, BlockPos pos) {
        //todo
        return ImmutableList.of();
    }

    @Nullable
    @Override
    public BlockPos getClosestStructure(String name, BlockPos pos, boolean findUnexplored) {
        //todo
        return BlockPos.ORIGIN;
    }
}
