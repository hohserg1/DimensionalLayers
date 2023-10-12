package net.minecraft.world

object WorldLens {
  def isChunkLoaded(world: World, x: Int, z: Int, allowEmpty: Boolean): Boolean = world.isChunkLoaded(x, z, allowEmpty)

}
