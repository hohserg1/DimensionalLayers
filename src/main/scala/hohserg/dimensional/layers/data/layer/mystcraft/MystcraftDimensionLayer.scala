package hohserg.dimensional.layers.data.layer.mystcraft

import com.xcompwiz.mystcraft.Mystcraft
import hohserg.dimensional.layers.data.layer.base.VanillaDimensionLayerBase
import hohserg.dimensional.layers.preset.spec.MystcraftLayerSpec
import hohserg.dimensional.layers.worldgen.proxy.client.ProxyWorldClient
import hohserg.dimensional.layers.{CCWorld, CCWorldClient, CCWorldServer}
import net.minecraft.world.DimensionType
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import java.util.HashSet as JHashSet

case class MystcraftDimensionLayer(_realStartCubeY: Int, spec: MystcraftLayerSpec, originalWorld: CCWorld)
  extends VanillaDimensionLayerBase(_realStartCubeY, originalWorld, spec.offsets) {
  override type Spec = MystcraftLayerSpec
  override type G = MystcraftDimensionGenerator

  override def dimensionType: DimensionType = Mystcraft.dimensionType

  override def dimensionId: Int = 1000 + bounds.realStartCubeY

  override protected def createGenerator(original: CCWorldServer): MystcraftDimensionGenerator = new MystcraftDimensionGenerator(original, this)

  @SideOnly(Side.CLIENT)
  override protected def createClientProxyWorld(original: CCWorldClient): ProxyWorldClient = {
    registerMystcraftDim()
    val proxyWorldClient = super.createClientProxyWorld(original)
    proxyWorldClient.provider.setDimension(dimensionId)
    proxyWorldClient
  }

  def registerMystcraftDim(): Unit = {
    if (!DimensionManager.isDimensionRegistered(dimensionId)) {
      DimensionManager.registerDimension(dimensionId, Mystcraft.dimensionType)
    } else {
      if (DimensionManager.getProviderType(dimensionId) != Mystcraft.dimensionType) {
        DimensionManager.unregisterDimension(dimensionId)
        DimensionManager.registerDimension(dimensionId, Mystcraft.dimensionType)
      }
    }
    if (Mystcraft.registeredDims == null)
      Mystcraft.registeredDims = new JHashSet[Integer]()
    Mystcraft.registeredDims.add(dimensionId)
  }
}
