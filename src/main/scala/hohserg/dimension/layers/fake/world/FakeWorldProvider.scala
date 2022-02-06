package hohserg.dimension.layers.fake.world

import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.{DimensionType, World, WorldProvider}

class FakeWorldProvider(biomeProviderFactory: World => BiomeProvider, _hasSkyLight: Boolean) extends WorldProvider {
  override def getDimensionType: DimensionType = DimensionType.OVERWORLD

  override def init(): Unit = {
    this.hasSkyLight = _hasSkyLight
    this.biomeProvider = biomeProviderFactory(world)
  }
}
