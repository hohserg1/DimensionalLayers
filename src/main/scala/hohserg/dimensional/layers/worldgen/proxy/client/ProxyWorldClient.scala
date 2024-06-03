package hohserg.dimensional.layers.worldgen.proxy.client

import hohserg.dimensional.layers.preset.DimensionalLayersPreset
import hohserg.dimensional.layers.worldgen.proxy.ProxyWorldCommon
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.WorldClient

class ProxyWorldClient(base: WorldClient)(val preset: DimensionalLayersPreset = DimensionalLayersPreset(base.getWorldInfo.getGeneratorOptions))
  extends BaseWorldClient(Minecraft.getMinecraft.getConnection, base.getWorldInfo, null, null, null) with ProxyWorldCommon {

}
