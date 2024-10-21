package hohserg.dimensional.layers.asm.dev.tweaks

import gloomyfolken.hooklib.api.{Hook, HookContainer, OnBegin, ReturnSolve}
import io.github.opencubicchunks.cubicchunks.core.util.CompatHandler

@HookContainer
object FixCCWatchdogPackageName {

  @Hook
  @OnBegin
  def getPackageName(ch: CompatHandler, clazz: Class[_]): ReturnSolve[String] =
    if (clazz.getCanonicalName == null) {
      val name = clazz.getName
      val dot = name.lastIndexOf('.')
      ReturnSolve.yes(if (dot < 0) "" else name.substring(0, dot))
    } else
      ReturnSolve.no()

}
