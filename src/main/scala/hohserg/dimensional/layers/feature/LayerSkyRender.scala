package hohserg.dimensional.layers.feature

import gloomyfolken.hooklib.api.*
import hohserg.dimensional.layers.*
import hohserg.dimensional.layers.data.LayerManagerClient
import hohserg.dimensional.layers.data.layer.base.DimensionalLayer
import hohserg.dimensional.layers.lens.RenderGlobalLens
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.{EntityRenderer, GlStateManager}
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.*;

@SideOnly(Side.CLIENT)
@HookContainer
object LayerSkyRender {

  def withLayerClientProxyWorld[A](world: World, entityIn: Entity, f: DimensionalLayer => A): Option[A] = {
    LayerManagerClient.getWorldData(world)
                      .flatMap(_.getLayerOf(entityIn))
                      .collect { case dimLayer: DimensionalLayer => f(dimLayer) }
  }

  def optionToReturnSolve[A](v: Option[A]): ReturnSolve[A] = {
    v.fold(ReturnSolve.no())(ReturnSolve.yes)
  }

  var lastSkyColor = new Vec3d(0, 0, 0)
  var lastSkyColorBody = new Vec3d(0, 0, 0)
  var lastFogColor = new Vec3d(0, 0, 0)

  @Hook
  @OnBegin
  def getSkyColor(world: World, entityIn: Entity, partialTicks: Float): Vec3d = {
    val currentColor = withLayerClientProxyWorld(world, entityIn, _.clientProxyWorld.getSkyColor(entityIn, partialTicks))
      .getOrElse(world.provider.getSkyColor(entityIn, partialTicks))
    val avgColor = lastSkyColor.scale(0.99).add(currentColor.scale(0.01))
    lastSkyColor = avgColor
    avgColor
  }

  def getSkyColorBody(world: World, entityIn: Entity, partialTicks: Float, realDimColor: Vec3d): Vec3d = {
    val currentColor = withLayerClientProxyWorld(world, entityIn, _.clientProxyWorld.getSkyColorBody(entityIn, partialTicks))
      .getOrElse(realDimColor)
    val avgColor = lastSkyColorBody.scale(0.99).add(currentColor.scale(0.01))
    lastSkyColorBody = avgColor
    avgColor
  }

  @Hook
  @OnBegin
  def getFogColor(world: World, partialTicks: Float): Vec3d = {
    val currentColor = withLayerClientProxyWorld(world, Minecraft.getMinecraft.getRenderViewEntity, _.clientProxyWorld.getFogColor(partialTicks))
      .getOrElse(world.provider.getFogColor(world.getCelestialAngle(partialTicks), partialTicks))
    val avgColor = lastFogColor.scale(0.99).add(currentColor.scale(0.01))
    lastFogColor = avgColor
    avgColor
  }

  @Hook
  @OnMethodCall(value = "renderSky", shift = Shift.INSTEAD)
  def renderWorldPass(entityRenderer: EntityRenderer, pass: Int, partialTicks: Float, finishTimeNano: Long): Unit = {
    val mc = Minecraft.getMinecraft
    val viewEntity = mc.getRenderViewEntity
    val renderGlobal = mc.renderGlobal

    val realWorld = mc.world

    LayerManagerClient.getWorldData(realWorld).foreach { data =>
      val start = viewEntity.posY.toInt
      val upper = data.getDimensionalLayerAt(start + 16)
      val current = data.getDimensionalLayerAt(start)
      val lower = data.getDimensionalLayerAt(start - 16)

      val localY = viewEntity.posY.toFloat - (start >> 4 << 4)

      renderSky(pass, partialTicks, current, transparency = 1)

      /* //todo: render neighboring sky with transparency
      if (current != lower) {
        renderSky(pass, partialTicks, lower, transparency = (15 - localY) / 15)
      }
      if (current != upper) {
        renderSky(pass, partialTicks, upper, transparency = localY / 15)
      }*/
    }

    mc.world = realWorld
    RenderGlobalLens.world.set(renderGlobal, realWorld)
  }

  private def renderSky(pass: Int, partialTicks: Float, maybeLayer: Option[DimensionalLayer], transparency: Float): Unit = {
    val mc = Minecraft.getMinecraft
    val renderGlobal = mc.renderGlobal
    maybeLayer match {
      case Some(layer) =>
        mc.world = layer.clientProxyWorld
        RenderGlobalLens.world.set(renderGlobal, layer.clientProxyWorld)
        GlStateManager.color(1, 1, 1, transparency)
        renderGlobal.renderSky(partialTicks, pass)
        GlStateManager.color(1, 1, 1, 1)
      case None =>
    }
  }
}