package hohserg.dimensional.layers.asm

import net.minecraftforge.common.ForgeVersion
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin

import java.util

@IFMLLoadingPlugin.MCVersion(value = ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(value = 5000)
class DimensionalLayersCoreMod extends IFMLLoadingPlugin {
  override def getASMTransformerClass: Array[String] = {
    Array("hohserg.dimensional.layers.asm.BaseWorldServerTransformer")
  }

  override def getModContainerClass: String = null

  override def getSetupClass: String = null

  override def injectData(data: util.Map[String, AnyRef]): Unit = ()

  override def getAccessTransformerClass: String = null
}
