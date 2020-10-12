package hohserg.dimensional.layers.config;

import hohserg.dimensional.layers.gens.LayerFiller;
import lombok.Value;
import net.minecraft.world.World;

@Value
public class LayerSpec {
    int minCubeY;
    int maxCubeY;
    FillerFactory filler;

    interface FillerFactory {
        LayerFiller apply(World world, int startY);
    }
}
