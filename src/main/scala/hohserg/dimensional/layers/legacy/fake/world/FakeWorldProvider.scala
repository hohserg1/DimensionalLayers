package hohserg.dimensional.layers.legacy.fake.world

import net.minecraft.world.{DimensionType, WorldProvider}

class FakeWorldProvider(originalProvider: WorldProvider, _hasSkyLight: Boolean) extends WorldProvider {
  override def getDimensionType: DimensionType = DimensionType.OVERWORLD

  override def init(): Unit = {
    this.hasSkyLight = _hasSkyLight
    this.biomeProvider = originalProvider.getBiomeProvider
  }
}
