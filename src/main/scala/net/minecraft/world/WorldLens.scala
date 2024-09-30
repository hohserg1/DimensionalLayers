package net.minecraft.world

import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.storage.loot.LootTableManager

object WorldLens {
  def isChunkLoaded(world: World, x: Int, z: Int, allowEmpty: Boolean): Boolean = world.isChunkLoaded(x, z, allowEmpty)

  def setChunkProvider(world: World, chunkProvider: IChunkProvider): Unit = {
    world.chunkProvider = chunkProvider
  }

  def setLootTable(world: World, lootTable: LootTableManager): Unit = {
    world.lootTable = lootTable
  }

}
