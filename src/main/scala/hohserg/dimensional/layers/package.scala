package hohserg.dimensional

import io.github.opencubicchunks.cubicchunks.api.world.{ICubicWorld, ICubicWorldServer}
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.world.{World, WorldServer}

import java.util.function.{BiFunction, Consumer}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

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

  implicit class RichOption[A](val x: Option[A]) extends AnyVal {
    def mapNull[B](f: A => B): Option[B] =
      x.flatMap(i => Option(f(i)))

    def toTry(msg: String): Try[A] =
      x.map(Success(_))
        .getOrElse(Failure(new NoSuchElementException(msg)))
  }

  def toLongSeed(str: String): Option[Long] =
    if (str.isEmpty)
      None
    else
      Some(Try(str.toLong)
        .filter(_ != 0)
        .getOrElse(str.hashCode.toLong))

  implicit class RichIntColor(val color: Int) extends AnyVal {
    def clearedAlpha: Int = color & 0x00FFffFF

    def withAlpha(a: Int): Int = clearedAlpha | (a << 24)

    def getAlpha: Int = (color >> 24) & 0xff
  }

}
