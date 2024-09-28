package hohserg.dimensional.layers.data.layer.base

import hohserg.dimensional.layers.preset.LayerSpec
import hohserg.dimensional.layers.{CCWorld, CCWorldServer}

trait Layer {
  type Spec <: LayerSpec
  type Bounds <: LayerBounds

  type G <: Generator

  def bounds: Bounds

  def spec: Spec

  def originalWorld: CCWorld

  protected def createGenerator(original: CCWorldServer): G

  lazy val generator: G = originalWorld match {
    case serverWorld: CCWorldServer => createGenerator(serverWorld)
  }

}
