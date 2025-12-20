package hohserg.dimensional.layers.data.layer.mystcraft

import com.xcompwiz.mystcraft.Mystcraft
import hohserg.dimensional.layers.CCWorldServer
import hohserg.dimensional.layers.data.layer.base.VanillaDimensionGeneratorBase
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer
import net.minecraft.world.WorldType
import net.minecraftforge.common.DimensionManager

import java.util.HashSet as JHashSet

class MystcraftDimensionGenerator(original: CCWorldServer, layer: MystcraftDimensionLayer)
  extends VanillaDimensionGeneratorBase[MystcraftDimensionLayer](original, layer, layer.spec.offsets) {

  override def seedOverride: Option[Long] = layer.spec.seedOverride

  override def worldType: WorldType = WorldType.DEFAULT

  override def worldTypePreset: String = ""

  override def beforeInitWorld(proxyWorld: ProxyWorldServer): Unit = {
    layer.registerMystcraftDim()
    proxyWorld.provider.setDimension(layer.dimensionId)
  }
}