package hohserg.dimensional

import java.util.function.{BiFunction, Consumer}
import scala.language.implicitConversions

package object layers {

  implicit def toJava[A, B, C](f: (A, B) => C): BiFunction[A, B, C] =
    new BiFunction[A, B, C] {
      override def apply(t: A, u: B): C = f(t, u)
    }

  implicit def toJava[A](f: A => Unit): Consumer[A] =
    new Consumer[A] {
      override def accept(t: A): Unit = f(t)
    }

}
