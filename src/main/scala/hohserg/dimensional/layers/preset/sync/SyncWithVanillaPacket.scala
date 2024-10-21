package hohserg.dimensional.layers.preset.sync

import gloomyfolken.hooklib.api.{Constants, Hook, OnReturn}
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.server.SPacketJoinGame
import net.minecraft.world.{EnumDifficulty, GameType, WorldType}
import net.minecraftforge.common.DimensionManager

//@HookContainer
object SyncWithVanillaPacket {

  @Hook(targetMethod = Constants.CONSTRUCTOR_NAME)
  def initPacketField(self: SPacketJoinGame,
                      playerIdIn: Int, gameTypeIn: GameType, hardcoreModeIn: Boolean, dimensionIn: Int, difficultyIn: EnumDifficulty, maxPlayersIn: Int, worldTypeIn: WorldType, reducedDebugInfoIn: Boolean): Unit = {
    AddFieldToPacket.generatorOptions.set(
      self,
      DimensionManager.getWorld(dimensionIn).getWorldInfo.getGeneratorOptions
    )
  }

  @Hook
  @OnReturn
  def writePacketData(packet: SPacketJoinGame, buf: PacketBuffer): Unit = {
    val generatorOptions = AddFieldToPacket.generatorOptions.get(packet)
    buf.writeInt(generatorOptions.length)
    buf.writeString(generatorOptions)
  }

  @Hook
  @OnReturn
  def readPacketData(packet: SPacketJoinGame, buf: PacketBuffer): Unit = {
    val len = buf.readInt()
    AddFieldToPacket.generatorOptions.set(packet, buf.readString(len))
  }

}
