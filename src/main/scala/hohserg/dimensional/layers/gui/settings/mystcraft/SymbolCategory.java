package hohserg.dimensional.layers.gui.settings.mystcraft;

public enum SymbolCategory {
    length("ModZero", "ModHalf", "ModFull", "ModDouble"),
    direction("ModNorth", "ModEast", "ModSouth", "ModWest"),
    phase("ModEnd", "ModRising", "ModNoon", "ModSetting"),
    color("ModGradient"),
    block,
    landscape("NoSea", "Caves", "TerrainAmplified", "TerrainEnd", "TerrainFlat", "TerrainNether", "TerrainNormal", "TerrainVoid"),
    structure("Dungeons", "FloatIslands", "FeatureLargeDummy", "FeatureMediumDummy", "FeatureSmallDummy", "HugeTrees", "LakesDeep", "LakesSurface", "Mineshafts", "NetherFort", "Obelisks", "Ravines", "TerModSpheres", "GenSpikes", "Strongholds", "Tendrils", "Villages", "CryForm", "Skylands", "StarFissure", "DenseOres"),
    biome("BioConGrid", "BioConNative", "BioConSingle", "BioConTiled", "BioConHuge", "BioConLarge", "BioConMedium", "BioConSmall", "BioConTiny" ),
    celestial("MoonDark", "MoonNormal", "StarsDark", "StarsEndSky", "StarsNormal", "StarsTwinkle", "SunDark", "SunNormal"),
    weather("LightingBright", "LightingDark", "LightingNormal", "WeatherOn", "WeatherCloudy", "WeatherFast", "WeatherNorm", "WeatherOff", "WeatherRain", "WeatherSlow", "WeatherSnow", "WeatherStorm"),
    sky("Rainbow", "NoHorizon", "ColorHorizon", "ColorCloud", "ColorCloudNat", "ColorSky", "ColorSkyNat", "ColorSkyNight"),
    visuals( "ColorFog", "ColorFogNat", "ColorFoliage", "ColorFoliageNat", "ColorGrass", "ColorGrassNat", "ColorWater", "ColorWaterNat"),
    other("PvPOff", "EnvAccel", "EnvExplosions", "EnvLightning", "EnvMeteor", "EnvScorch", "ModClear");

    public final String[] symbols;

    SymbolCategory(String... symbols) {
        this.symbols = symbols;
    }
}
