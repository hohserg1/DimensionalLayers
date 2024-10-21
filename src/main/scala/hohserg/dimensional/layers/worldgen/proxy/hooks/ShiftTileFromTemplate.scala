package hohserg.dimensional.layers.worldgen.proxy.hooks

import gloomyfolken.hooklib.api._
import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.structure.template.{ITemplateProcessor, PlacementSettings, Template}

import javax.annotation.Nullable

@HookContainer
object ShiftTileFromTemplate {
  @Hook
  @OnMethodCall("readFromNBT")
  def addBlocksToWorld(self: Template,
                       worldIn: World, pos: BlockPos, @Nullable templateProcessor: ITemplateProcessor, placementIn: PlacementSettings, flags: Int,
                       @LocalVariable(id = 15) tileentity2: TileEntity
                      ): Unit = {
    worldIn match {
      case proxy: ProxyWorldServer =>
        tileentity2.setPos(proxy.bounds.shift(tileentity2.getPos))
      case _ =>
    }
  }
}
