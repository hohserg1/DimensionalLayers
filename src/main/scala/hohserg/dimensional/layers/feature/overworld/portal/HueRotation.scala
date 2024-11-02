package hohserg.dimensional.layers.feature.overworld.portal

/**
 * based on created by Mark Ransom: https://stackoverflow.com/questions/8507885/shift-hue-of-an-rgb-color/8510751#8510751
 *
 * @param angle in radians
 */
case class HueRotation(angle: Double) {
  val matrix: Seq[Seq[Double]] = {
    val cosA = math.cos(angle)
    val sinA = math.sin(angle)
    val sqrt_1_3 = math.sqrt(1 / 3d)
    Seq(
      Seq(cosA + (1 - cosA) / 3, 1 / 3d * (1 - cosA) - sqrt_1_3 * sinA, 1 / 3d * (1 - cosA) + sqrt_1_3 * sinA),
      Seq(1 / 3d * (1 - cosA) + sqrt_1_3 * sinA, cosA + 1 / 3d * (1 - cosA), 1 / 3d * (1 - cosA) - sqrt_1_3 * sinA),
      Seq(1 / 3d * (1 - cosA) - sqrt_1_3 * sinA, 1 / 3d * (1 - cosA) + sqrt_1_3 * sinA, cosA + 1 / 3d * (1 - cosA))
    )
  }

  def apply(color: Int): Int = {
    def clamp(v: Double): Int =
      if (v < 0)
        0
      else if (v > 255)
        255
      else
        (v + 0.5).toInt

    val a = color & 0xff000000
    val r = (color >> 16) & 0xff
    val g = (color >> 8) & 0xff
    val b = color & 0xff

    a |
      (clamp(r * matrix(0)(0) + g * matrix(0)(1) + b * matrix(0)(2)) << 16) |
      (clamp(r * matrix(1)(0) + g * matrix(1)(1) + b * matrix(1)(2)) << 8) |
      clamp(r * matrix(2)(0) + g * matrix(2)(1) + b * matrix(2)(2))
  }
}

object HueRotation {
  var purpleToGreen = HueRotation(math.toRadians(-150))

}
