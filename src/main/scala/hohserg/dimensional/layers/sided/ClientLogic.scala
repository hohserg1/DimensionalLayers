package hohserg.dimensional.layers.sided

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.toasts.SystemToast
import net.minecraft.util.text.TextComponentString

class ClientLogic extends CommonLogic {

  override def printError(msg: String, e: Throwable): Unit = {
    super.printError(msg, e)
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
