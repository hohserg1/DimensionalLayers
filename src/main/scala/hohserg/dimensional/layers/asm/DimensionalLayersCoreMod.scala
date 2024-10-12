package hohserg.dimensional.layers.asm

import net.minecraft.launchwrapper.Launch
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
    Mixins.addConfiguration("dimensional_layers.mixins.compat_events.json")
    //Mixins.addConfiguration("dimensional_layers.mixins.sync_preset.json")
    Mixins.addConfiguration("dimensional_layers.mixins.catch_teleporter.json")
    if (Launch.blackboard.get("fml.deobfuscatedEnvironment").asInstanceOf[Boolean])
      Mixins.addConfiguration("dimensional_layers.mixins.dev_tweaks.json")

    //Mixins.addConfiguration("dimensional_layers.mixins.compat_geographicraft.json")
    Mixins.addConfiguration("dimensional_layers.mixins.compat_otg.json")

    Array("hohserg.dimensional.layers.asm.BaseWorldServerTransformer")
  }

  override def getModContainerClass: String = null

  override def getSetupClass: String = null

  override def injectData(data: util.Map[String, AnyRef]): Unit = ()

  override def getAccessTransformerClass: String = null
}
