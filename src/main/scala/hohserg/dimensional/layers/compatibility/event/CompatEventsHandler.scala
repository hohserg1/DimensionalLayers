package hohserg.dimensional.layers.compatibility.event

import hohserg.dimensional.layers.Configuration
import hohserg.dimensional.layers.Configuration.CompatibilityFeatures.LayerRelatedEvents.EntityContext
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.MinecraftForge

object CompatEventsHandler {

  def init(): Unit = {
    val layer_related_events = Configuration.compatibility_features.layer_related_events;

    val commonEventConfigs: Map[Array[String], EventHandlerFactory] = Map(
      layer_related_events.living_update_event.triggeredMods -> (new LivingUpdateEventHandler(_,
        layer_related_events.living_update_event.entityContext match {
          case EntityContext.player => _.isInstanceOf[EntityPlayer]
          case EntityContext.any_entity => _ => true
        }
      )) //,
      //layer_related_events.RenderTickEvent -> (new RenderTickEventHandler(_)),
      //layer_related_events.FogDensity -> (new FogDensityHandler(_)),
      //layer_related_events.FogColors -> (new FogColorsHandler(_)),
      //layer_related_events.RenderWorldLastEvent -> (new RenderWorldLastEventHandler(_)),
      //layer_related_events.DrawBlockHighlightEvent -> (new DrawBlockHighlightEventHandler(_))
    )

    commonEventConfigs.foreach { case (triggeredMods, handlerFactory) =>
      val modidSet = Set(triggeredMods: _*)
      if (modidSet.nonEmpty) {
        MinecraftForge.EVENT_BUS.register(handlerFactory(modidSet))
      }
    }
  }

  type EventHandlerFactory = Set[String] => Any
}
