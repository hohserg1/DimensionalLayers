package hohserg.dimensional.layers.sided

import hohserg.dimensional.layers.Main

class CommonLogic {

  def printInfo(msg: String): Unit = {
    print("INFO", msg)
  }

  def printError(msg: String, e: Throwable): Unit = {
    print("ERROR", msg)
    e.printStackTrace()
  }

  def printWarning(msg: String, e: Throwable): Unit = {
    print("WARNING", msg + " " + e.getMessage)
  }

  def print(level: String, msg: String): Unit = {
    println("[" + Main.name + "][" + level + "]: " + msg)
  }

}
