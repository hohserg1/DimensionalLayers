package hohserg.dimensional.layers.sided

import hohserg.dimensional.layers.Main
import hohserg.dimensional.layers.compatibility.event.CompatEventsHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent

class CommonLogic {

  def init(e: FMLInitializationEvent): Unit = {
    CompatEventsHandler.init()
  }

  def printInfo(msg: String): Unit = {
    print("INFO", msg)
  }

  def printGenerationError(msg: String, contextSeed: Long, contextPreset: String, e: Throwable): Unit = {
    printError(msg, "Context(seed=" + contextSeed + ", preset=" + contextPreset + ")", e)
  }

  def printSimpleError(msg: String, dest: String): Unit = {
    print("ERROR][simple", msg + ":" + dest)
  }

  def printError(msg: String, context: String, e: Throwable): Unit = {
    print("ERROR" + (if (e.getStackTrace.nonEmpty) "][useful" else ""), msg)
    print("ERROR", context)
    e.printStackTrace()
  }

  def printWarning(msg: String, e: Throwable): Unit = {
    print("WARNING", msg + " " + e.getMessage)
  }

  def print(level: String, msg: String): Unit = {
    println("[" + Main.name + "][" + level + "]: " + msg)
  }

}
