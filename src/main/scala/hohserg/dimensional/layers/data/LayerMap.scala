package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.data.LayerMap.{Slice1, shift, slice1Coord}
import hohserg.dimensional.layers.data.layer.base.Layer
import io.github.opencubicchunks.cubicchunks.api.util.IntRange

object LayerMap {
  private val minBlockY = -1073741824
  private val maxBlockY = 1073741823

  private val totalWorldSizeBlocks = maxBlockY - minBlockY + 1

  val minCubeY = minBlockY >> 4
  val maxCubeY = maxBlockY >> 4

  private val totalWorldSizeCubes = maxCubeY - minCubeY + 1

  private val totalWorldExponent = log2(totalWorldSizeCubes) //27
  private val sliceExponent_1 = 18
  private val sliceExponent_2 = 9

  private sealed trait Slice1 {
    def get(shiftedCubeY: Int): Option[Layer]
  }

  private class Slice1Solid(all: Layer) extends Slice1 {
    val r = Some(all)

    override def get(shiftedCubeY: Int): Option[Layer] = r
  }

  private class Slice1Branch(val subslices: Array[Option[Slice2]]) extends Slice1 {
    override def get(shiftedCubeY: Int): Option[Layer] = subslices(slice2Coord(shiftedCubeY)).flatMap(_.get(shiftedCubeY))
  }

  private sealed trait Slice2 {
    def get(shiftedCubeY: Int): Option[Layer]
  }

  private class Slice2Solid(all: Layer) extends Slice2 {
    val r = Some(all)

    override def get(shiftedCubeY: Int): Option[Layer] = r
  }

  private class Slice2Branch(val cubes: Array[Option[Layer]]) extends Slice2 {
    override def get(shiftedCubeY: Int): Option[Layer] = cubes(localCubeCoord(shiftedCubeY))
  }

  private val shift = 1073741824 >> 4

  private val nineOnes = 511

  private def slice1Coord(shiftedCubeY: Int): Int =
    shiftedCubeY >> sliceExponent_1 & nineOnes

  private def slice2Coord(shiftedCubeY: Int): Int =
    shiftedCubeY >> sliceExponent_2 & nineOnes

  private def localCubeCoord(shiftedCubeY: Int): Int =
    shiftedCubeY & nineOnes

  private def coords(cubeY: Int): (Int, Int, Int) = {
    val shiftedCubeY = cubeY + shift
    (
      slice1Coord(shiftedCubeY),
      slice2Coord(shiftedCubeY),
      localCubeCoord(shiftedCubeY)
    )
  }

  def apply(seq: Seq[(IntRange, Layer)]): LayerMap = {
    val slices: Array[Option[Slice1]] = new Array(512)

    for (i <- slices.indices)
      slices(i) = None

    for {
      (range, layer) <- seq

      sliceEntry_1 = Some(new Slice1Solid(layer))
      sliceEntry_2 = Some(new Slice2Solid(layer))
      cubeEntry = Some(layer)

      (fromSlice_1, fromSlice_2, fromLocalCube) = coords(range.getMin)

      (toSlice_1, toSlice_2, toLocalCube) = coords(range.getMax)


      fromFullSlice_1 = if (fromSlice_2 == 0 && fromLocalCube == 0) fromSlice_1 else fromSlice_1 + 1
      toFullSlice_1 = if (toSlice_2 == nineOnes && toLocalCube == nineOnes) toSlice_1 else toSlice_1 - 1

      fromFullSlice_2 = if (fromLocalCube == 0) fromSlice_2 else fromSlice_2 + 1
      toFullSlice_2 = if (toLocalCube == nineOnes) toSlice_2 else toSlice_2 - 1
    } {
      for (slice_1 <- fromFullSlice_1 to toFullSlice_1)
        slices(slice_1) = sliceEntry_1

      def getOrInitSubslices(slice_1: Int): Array[Option[Slice2]] =
        slices(slice_1) match {
          case Some(subsliceBranch: Slice1Branch) => subsliceBranch.subslices
          case None =>
            val subslices = new Array[Option[Slice2]](512)
            for (i <- 0 to nineOnes)
              subslices(i) = None
            slices(slice_1) = Some(new Slice1Branch(subslices))
            subslices
        }

      def getOrInitSubcubes(subslices: Array[Option[Slice2]], slice_2: Int): Array[Option[Layer]] =
        subslices(slice_2) match {
          case Some(cubeBranch: Slice2Branch) => cubeBranch.cubes
          case None =>
            val cubes = new Array[Option[Layer]](512)
            for (i <- 0 to nineOnes)
              cubes(i) = None
            subslices(slice_2) = Some(new Slice2Branch(cubes))
            cubes
        }

      if (fromSlice_2 > 0) {
        val subslices = getOrInitSubslices(fromSlice_1)

        for (i <- fromFullSlice_2 to (if (fromSlice_1 == toSlice_1) toFullSlice_2 else nineOnes))
          subslices(i) = sliceEntry_2
      }

      if (fromLocalCube > 0) {
        val subslices: Array[Option[Slice2]] = getOrInitSubslices(fromSlice_1)

        val cubes: Array[Option[Layer]] = getOrInitSubcubes(subslices, fromSlice_2)

        for (i <- fromLocalCube to (if (fromSlice_2 == toSlice_2) toLocalCube else nineOnes))
          cubes(i) = cubeEntry
      }

      if (toSlice_2 < nineOnes) {
        val subslices = getOrInitSubslices(toSlice_1)

        for (i <- (if (fromSlice_1 == toSlice_1) fromFullSlice_2 else 0) to toFullSlice_2)
          subslices(i) = sliceEntry_2
      }

      if (toLocalCube < nineOnes) {
        val subslices = getOrInitSubslices(toSlice_1)

        val cubes = getOrInitSubcubes(subslices, toSlice_2)

        for (i <- (if (fromSlice_2 == toSlice_2) fromLocalCube else 0) to toLocalCube)
          cubes(i) = cubeEntry
      }
    }
    new LayerMap(slices)
  }


  def log2(v: Int): Int = {
    var bits = v
    var log = 0
    if ((bits & 0xffff0000) != 0) {
      bits >>>= 16
      log = 16
    }
    if (bits >= 256) {
      bits >>>= 8
      log += 8
    }
    if (bits >= 16) {
      bits >>>= 4
      log += 4
    }
    if (bits >= 4) {
      bits >>>= 2
      log += 2
    }
    log + (bits >>> 1)
  }

}

class LayerMap private(slices: Array[Option[Slice1]]) {
  def get(cubeY: Int): Option[Layer] = {
    val shiftedCubeY = cubeY + shift
    slices(slice1Coord(shiftedCubeY)).flatMap(_.get(shiftedCubeY))
  }
}
