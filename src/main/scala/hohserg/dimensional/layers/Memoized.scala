package hohserg.dimensional.layers

import java.util.function

object Memoized {
  def apply[A, B](f: A => B): A => B = {
    val cache = new java.util.HashMap[A, B]()
    val javaFunctionWrapper = new function.Function[A, B] {
      override def apply(t: A): B = f(t)
    }
    cache.computeIfAbsent(_, javaFunctionWrapper)
  }
}
