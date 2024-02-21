package hohserg.dimensional.layers.worldgen.proxy;

import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer;
import io.github.opencubicchunks.cubicchunks.api.world.IColumn;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public interface BaseProxyCube extends ICube {

    ProxyWorldServer proxyWorld();

    @Override
    default <T extends World & ICubicWorld> T getWorld() {
        return (T) proxyWorld();
    }

    @Override
    default <T extends Chunk & IColumn> T getColumn() {
        return (T) proxyWorld().getChunk(getX(), getZ());
    }
}
