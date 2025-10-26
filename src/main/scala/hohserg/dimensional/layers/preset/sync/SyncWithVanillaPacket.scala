package hohserg.dimensional.layers.preset.sync

import gloomyfolken.hooklib.api.*
import hohserg.dimensional.layers.DimensionalLayersWorldType
import hohserg.dimensional.layers.data.LayerManagerClient
import hohserg.dimensional.layers.lens.{NetHandlerPlayClientLens, WorldInfoLens}
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.server.SPacketJoinGame
import net.minecraft.world.{EnumDifficulty, GameType, WorldType}
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@HookContainer
object SyncWithVanillaPacket {

  @Hook(targetMethod = Constants.CONSTRUCTOR_NAME)
  def initPacketField(self: SPacketJoinGame,
                      playerIdIn: Int, gameTypeIn: GameType, hardcoreModeIn: Boolean, dimensionIn: Int, difficultyIn: EnumDifficulty, maxPlayersIn: Int, worldTypeIn: WorldType, reducedDebugInfoIn: Boolean): Unit = {
    val worldInfo = DimensionManager.getWorld(0).getWorldInfo
    AddFieldToPacket.generatorOptions.set(self, worldInfo.getGeneratorOptions)
    AddFieldToPacket.isDimensionalLayersWorldType.set(self, worldInfo.getTerrainType == DimensionalLayersWorldType)
  }

  @Hook
  @OnReturn
  def writePacketData(packet: SPacketJoinGame, buf: PacketBuffer): Unit = {
    buf.writeBoolean(AddFieldToPacket.isDimensionalLayersWorldType.get(packet))

    val generatorOptions = AddFieldToPacket.generatorOptions.get(packet)
    buf.writeInt(generatorOptions.length)
    buf.writeString(generatorOptions)
  }

  @Hook
  @OnReturn
  def readPacketData(packet: SPacketJoinGame, buf: PacketBuffer): Unit = {
    AddFieldToPacket.isDimensionalLayersWorldType.set(packet, buf.readBoolean())

    val len = buf.readInt()
    AddFieldToPacket.generatorOptions.set(packet, buf.readString(len))
  }


  @SideOnly(Side.CLIENT)
  @Hook
  @OnMethodCall(value = "loadWorld", shift = Shift.BEFORE)
  def handleJoinGame(netHandler: NetHandlerPlayClient, packet: SPacketJoinGame): Unit = {
    val world = NetHandlerPlayClientLens.world.get(netHandler)
    val preset = AddFieldToPacket.generatorOptions.get(packet)
    if (world.provider.getDimension == 0) {
      WorldInfoLens.generatorOptions.set(world.getWorldInfo, preset)
    }
    if (AddFieldToPacket.isDimensionalLayersWorldType.get(packet))
      LayerManagerClient.onPresetPacket(preset)
    else
      LayerManagerClient.clear()
  }

}
