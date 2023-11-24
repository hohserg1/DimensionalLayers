package hohserg.dimensional.layers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = "dimension_layers")
@Mod.EventBusSubscriber
public class Configuration {
    public static String defaultPreset = "";

    public static boolean worldTypeByDefault = true;

    @SubscribeEvent
    public static void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        ConfigManager.sync(Main.modid(), Config.Type.INSTANCE);
    }
}
