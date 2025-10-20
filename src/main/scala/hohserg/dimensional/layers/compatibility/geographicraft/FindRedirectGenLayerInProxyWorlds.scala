package hohserg.dimensional.layers.compatibility.geographicraft

import climateControl.customGenLayer.GenLayerRiverMixWrapper
import com.Zeno410Utils.Maybe
import gloomyfolken.hooklib.api.{Hook, HookContainer, OnBegin}
import hohserg.dimensional.layers.CCWorldServer
import hohserg.dimensional.layers.data.LayerManagerServer
import hohserg.dimensional.layers.data.layer.base.DimensionalGenerator
import hohserg.dimensional.layers.lens.BiomeProviderLens
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer
import net.minecraft.world.WorldServer
import net.minecraft.world.gen.layer.GenLayerRiverMix
import net.minecraftforge.common.DimensionManager

import scala.util.boundary
import scala.util.boundary.break

@HookContainer
object FindRedirectGenLayerInProxyWorlds {

  @Hook
  @OnBegin
  def findSelf(self: GenLayerRiverMixWrapper): Unit = {
    boundary:
      if (!AccessorGenLayerRiverMixWrapper.found.get(self)) {
        for {
          id <- DimensionManager.getIDs
          data <- LayerManagerServer.getWorldData(DimensionManager.getWorld(id).asInstanceOf[CCWorldServer])
          (_, layer) <- data.layers
        } {
          layer.generator match {
            case generator: DimensionalGenerator =>
              val proxyWorld: ProxyWorldServer = generator.proxyWorld
              if (BiomeProviderLens.genBiomes.get(proxyWorld.getBiomeProvider) eq self) {
                val gcLayers: Maybe[GenLayerRiverMix] =
                  AccessorGenLayerRiverMixWrapper.dimensionManager.get(self)
                                                 .getGeographicraftGenlayers(
                                                   proxyWorld.asInstanceOf[WorldServer],
                                                   proxyWorld.provider.getDimension, AccessorGenLayerRiverMixWrapper.original.get(self)
                                                 )
                if (gcLayers.isKnown) {
                  AccessorGenLayerRiverMixWrapper.redirect.set(self, gcLayers.iterator.next)
                  AccessorGenLayerRiverMixWrapper.found.set(self, true)
                  break()
                }
              }
            case _ =>
          }
        }
      }
  }
}
