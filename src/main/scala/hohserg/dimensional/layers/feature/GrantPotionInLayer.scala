package hohserg.dimensional.layers.feature

import hohserg.dimensional.layers.data.{LayerManager, LayerManagerClient, LayerManagerServer}
import hohserg.dimensional.layers.preset.spec.PotionEffectGranting
import net.minecraft.potion.PotionEffect
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent

@EventBusSubscriber
object GrantPotionInLayer {

  @SubscribeEvent
  def tick(e: PlayerTickEvent): Unit = {
    val world = e.player.getEntityWorld
    LayerManager.getSided(world).getWorldData(world) match {
      case Some(data) =>
        data.getLayerOf(e.player).filter(l => l.hasPotionEffectGranting).foreach(l => {
          l.spec.additionalFeatures.foreach {
            case PotionEffectGranting(effect, amplifier, playerOnly) =>
              e.player.addPotionEffect(new PotionEffect(effect, 20 * 11, amplifier, true, false))
            case _ =>
          }
        })
      case None =>
    }
  }

}
