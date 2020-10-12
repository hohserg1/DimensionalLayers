package hohserg.dimensional.layers;

import hohserg.dimensional.layers.config.LayerSpec;
import hohserg.dimensional.layers.config.LayersConfig;
import io.github.opencubicchunks.cubicchunks.api.util.IntRange;
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType;
import io.github.opencubicchunks.cubicchunks.api.worldgen.ICubeGenerator;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;

import javax.annotation.Nullable;
import java.util.Comparator;

public class DimensionLayersWorldType extends WorldType implements ICubicWorldType {
    public DimensionLayersWorldType() {
        super("dime_layers");
    }

    @Nullable
    @Override
    public ICubeGenerator createCubeGenerator(World world) {
        return new DimensionLayersGenerator(world);
    }

    @Override
    public IntRange calculateGenerationHeightRange(WorldServer world) {
        return IntRange.of(
                LayersConfig.layers.stream().map(LayerSpec::getMinCubeY).min(Comparator.naturalOrder()).get() << 4,
                (LayersConfig.layers.stream().map(LayerSpec::getMaxCubeY).min(Comparator.naturalOrder()).get() + 1) << 4);
    }

    @Override
    public boolean hasCubicGeneratorForWorld(World object) {
        return object.provider.getDimension() == 0;
    }
}
