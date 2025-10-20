package hohserg.dimensional.layers.lens;

import gloomyfolken.hooklib.api.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.storage.*;
import net.minecraft.world.storage.loot.*;
import org.apache.commons.lang3.*;

@HookContainer
public class WorldLens {

    @FieldLens
    public static FieldAccessor<World, IChunkProvider> chunkProvider;

    @FieldLens
    public static FieldAccessor<World, LootTableManager> lootTable;

    @FieldLens
    public static FieldAccessor<World, WorldInfo> worldInfo;
    
    @MethodLens
    public static boolean isChunkLoaded(World self, int var1, int var2, boolean var3){
        throw new NotImplementedException("hooklib failed to method lens");
    }
}
