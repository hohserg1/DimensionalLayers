package hohserg.dimensional

import io.github.opencubicchunks.cubicchunks.api.world.{ICubicWorld, ICubicWorldServer}
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.world.{World, WorldServer}

import java.util.function.{BiFunction, Consumer}
import scala.language.implicitConversions

package object layers {

  type CCWorld = World with ICubicWorld

  type CCWorldServer = WorldServer with ICubicWorldServer

  type CCWorldClient = WorldClient with ICubicWorld

  implicit def toJava[A, B, C](f: (A, B) => C): BiFunction[A, B, C] =
    new BiFunction[A, B, C] {
      override def apply(t: A, u: B): C = f(t, u)
    }

  implicit def toJava[A](f: A => Unit): Consumer[A] =
    new Consumer[A] {
      override def accept(t: A): Unit = f(t)
    }

  implicit def toJava[A, B](f: A => B): java.util.function.Function[A, B] =
    new java.util.function.Function[A, B] {
      override def apply(t: A): B = f(t)
    }

  def clamp[N: Numeric](v: N, minV: N, maxV: N): N = {
    val n = implicitly[Numeric[N]]
    import n._
    minV max v min maxV
  }

}
