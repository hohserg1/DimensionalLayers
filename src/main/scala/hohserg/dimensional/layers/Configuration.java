package hohserg.dimensional.layers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = "dimensional_layers")
@Mod.EventBusSubscriber
public class Configuration {
    @SubscribeEvent
    public static void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        ConfigManager.sync(Main.modid(), Config.Type.INSTANCE);
    }

    public static String defaultPreset = "";

    @Config.Comment("if true, default selected world type will be dimlayers at new world creation gui")
    public static boolean worldTypeByDefault = true;

    @Config.Comment("its optional features which can improve compatibility with some mods")
    public static CompatibilityFeatures compatibility_features = new CompatibilityFeatures();

    public static class CompatibilityFeatures {

        @Config.Comment("inner sections meaning settings for triggering common forge events with layer-related fake world")
        public LayerRelatedEvents layer_related_events = new LayerRelatedEvents();

        public static class LayerRelatedEvents {
            public enum EntityContext {
                @Config.Comment("for only players") player,
                @Config.Comment("for any entities") any_entity
            }

            public static class LivingUpdateEventConfig {
                @Config.Comment("for which entities will be triggered event")
                public EntityContext entityContext = EntityContext.player;

                @Config.Comment({
                        "list if modid's for which will be triggered event",
                        "as default here MistyWorld modid bc of fog damage feature support",
                })
                public String[] triggeredMods = {"mist"};
            }

            @Config.Comment("LivingUpdateEvent")
            public LivingUpdateEventConfig living_update_event = new LivingUpdateEventConfig();

            @Config.Comment({
                    "list if modid's for which will be triggered RenderTickEvent with layer-related fake world",
                    "as default here MistyWorld modid bc of fog render feature support",
            })
            public String[] RenderTickEvent = {"mist"};

            @Config.Comment({
                    "list if modid's for which will be triggered FogDensity with layer-related fake world",
                    "as default here MistyWorld modid bc of fog render feature support",
            })
            public String[] FogDensity = {"mist"};

            @Config.Comment({
                    "list if modid's for which will be triggered FogColors with layer-related fake world",
                    "as default here MistyWorld modid bc of fog render feature support",
            })
            public String[] FogColors = {"mist"};

            @Config.Comment({
                    "list if modid's for which will be triggered RenderWorldLastEvent with layer-related fake world",
                    "as default here MistyWorld modid bc of fog render feature support",
            })
            public String[] RenderWorldLastEvent = {"mist"};

            @Config.Comment({
                    "list if modid's for which will be triggered DrawBlockHighlightEvent with layer-related fake world",
                    "as default here MistyWorld modid bc of fog render feature support",
            })
            public String[] DrawBlockHighlightEvent = {"mist"};

        }
    }
}
