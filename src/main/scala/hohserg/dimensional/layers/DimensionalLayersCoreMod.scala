package hohserg.dimensional.layers

import net.minecraftforge.common.ForgeVersion
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import org.spongepowered.asm.mixin.Mixins

import java.util

@IFMLLoadingPlugin.MCVersion(value = ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(value = 5000)
class DimensionalLayersCoreMod extends IFMLLoadingPlugin {
  override def getASMTransformerClass: Array[String] = {
    Mixins.addConfiguration("dimensional_layers.mixins.proxy.json")
    Mixins.addConfiguration("dimensional_layers.mixins.gui.json")

    if (System.getProperty("net.minecraftforge.gradle.GradleStart.srg.srg-mcp") != null)
      Array(
        "io.github.opencubicchunks.cubicchunks.cubicgen.asm.coremod.MapGenStrongholdCubicConstructorTransform",
        "io.github.opencubicchunks.cubicchunks.cubicgen.asm.coremod.MalisisCoreAT"
      )
    else
      Array(
        "io.github.opencubicchunks.cubicchunks.cubicgen.asm.coremod.MapGenStrongholdCubicConstructorTransform"
      )
  }

  override def getModContainerClass: String = null

  override def getSetupClass: String = null

  override def injectData(data: util.Map[String, AnyRef]): Unit = ()

  override def getAccessTransformerClass: String = null
}