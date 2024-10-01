package hohserg.dimensional.layers.sided

import hohserg.dimensional.layers.Main

class CommonLogic {

  def printInfo(msg: String): Unit = {
    print("INFO", msg)
  }

  def printGenerationError(msg: String, contextSeed: Long, contextPreset: String, e: Throwable): Unit = {
    printError(msg, "Context(seed=" + contextSeed + ", preset=" + contextPreset + ")", e)
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
