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
    showErrorMsgClient(msg, e)
  }

  override def printSimpleError(msg: String, dest: String): Unit = {
    super.printSimpleError(msg, dest)
    showErrorMsgClient(msg, dest)
  }

  private def showErrorMsgClient(title: String, exception: Throwable): Unit = {
    showErrorMsgClient(title, exception.getMessage + "\nfull stacktrace in log")
  }

  private def showErrorMsgClient(title: String, desc: String): Unit = {
    Minecraft.getMinecraft.getToastGui.add(new SystemToast(
      SystemToast.Type.NARRATOR_TOGGLE,
      new TextComponentString(title),
      new TextComponentString(desc)
    ))
  }

}
