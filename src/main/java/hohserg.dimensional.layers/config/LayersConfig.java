package hohserg.dimensional.layers.config;


import com.google.common.collect.ImmutableList;
import hohserg.dimensional.layers.gens.EdenLayer;
import hohserg.dimensional.layers.gens.LayerFiller;
import hohserg.dimensional.layers.gens.TestLayer;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static hohserg.dimensional.layers.gens.LayerFiller.voidFiller;
import static java.util.stream.Collectors.toMap;

public class LayersConfig {
    public static List<LayerSpec> layers = ImmutableList.<LayerSpec>builder()
            .add(new LayerSpec(0, 15, EdenLayer::new))
            .add(new LayerSpec(-1, -1, TestLayer::new))
            .build();

    private static Map<Integer, LayerFiller> fillerCache;

    public static void init(World world) {
        fillerCache = layers.stream()
                .flatMap(layerSpec -> {
                    LayerFiller filler = layerSpec.getFiller().apply(world, layerSpec.getMinCubeY() << 4);
                    return IntStream.rangeClosed(layerSpec.getMinCubeY(), layerSpec.getMaxCubeY())
                            .mapToObj(cubeY -> Pair.of(cubeY, filler));
                })
                .collect(toMap(Pair::getLeft, Pair::getRight));
    }

    public static LayerFiller getLayerFiller(int cubeY) {
        return fillerCache.getOrDefault(cubeY, voidFiller);
    }
}
