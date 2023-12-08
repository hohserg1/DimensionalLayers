package hohserg.dimensional.layers.worldgen.proxy;

import net.minecraft.profiler.Profiler;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nullable;

public abstract class BaseWorldServer extends World {
    public BaseWorldServer(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn) {
        super(saveHandlerIn, info, providerIn, profilerIn, false);
    }

    @Override
    public IChunkProvider getChunkProvider() {
        return chunkProvider;
    }

    public void flushToDisk() {
    }

    public void saveAllChunks(boolean all, @Nullable IProgressUpdate progressCallback) throws MinecraftException {
    }
}
