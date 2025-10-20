package hohserg.dimensional

import io.github.opencubicchunks.cubicchunks.api.world.{ICubicWorld, ICubicWorldServer}
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.world.{World, WorldServer}

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

package object layers {

  type CCWorld = World & ICubicWorld

  type CCWorldServer = WorldServer & ICubicWorldServer

  type CCWorldClient = WorldClient & ICubicWorld

  def clamp[N: Numeric](v: N, minV: N, maxV: N): N = {
    val n = implicitly[Numeric[N]]
    import n.*
    minV max v min maxV
  }

  implicit class RichOption[A](val x: Option[A]) extends AnyVal {
    def mapNull[B](f: A => B): Option[B] = {
      x.flatMap(i => Option(f(i)))
    }

    def toTry(msg: String): Try[A] = {
      x.map(Success(_))
       .getOrElse(Failure(new NoSuchElementException(msg)))
    }
  }

  def toLongSeed(str: String): Option[Long] = {
    if (str.isEmpty)
      None
    else
      Some(Try(str.toLong)
        .filter(_ != 0)
        .getOrElse(str.hashCode.toLong))
  }

  implicit class RichIntColor(val color: Int) extends AnyVal {
    def clearedAlpha: Int = color & 0x00FFffFF

    def withAlpha(a: Int): Int = clearedAlpha | (a << 24)

    def getAlpha: Int = (color >> 24) & 0xff
  }


  def println(v: Any*): Unit = {
    if (v.size == 1)
      Predef.println(v.head)
    else
      Predef.println(v.mkString("(", ", ", ")"))
  }

}
