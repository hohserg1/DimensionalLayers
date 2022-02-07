package hohserg.dimension

import java.util.function.BiFunction
import scala.language.implicitConversions

package object layers {

  implicit def toJava[A, B, C](f: (A, B) => C): BiFunction[A, B, C] =
    new BiFunction[A, B, C] {
      override def apply(t: A, u: B): C = f(t, u)
    }

}
