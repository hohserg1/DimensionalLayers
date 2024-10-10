package hohserg.dimensional.layers.sided

import hohserg.dimensional.layers.compatibility.event.CompatEventsHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.toasts.SystemToast
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.event.FMLInitializationEvent

class ClientLogic extends CommonLogic {
  override def init(e: FMLInitializationEvent): Unit = {
    super.init(e)
    CompatEventsHandler.initClient()
  }

  override def printError(msg: String, context: String, e: Throwable): Unit = {
    super.printError(msg, context, e)
    showErrorMsgClient(e, msg)
  }

  private def showErrorMsgClient(exception: Throwable, title: String): Unit = {
    Minecraft.getMinecraft.getToastGui.add(new SystemToast(
      SystemToast.Type.NARRATOR_TOGGLE,
      new TextComponentString(title),
      new TextComponentString(exception.getMessage + "\nfull stacktrace in log")
    ))
  }

}
