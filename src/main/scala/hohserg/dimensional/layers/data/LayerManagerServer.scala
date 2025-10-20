package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.{CCWorldServer, Main}
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.Optional

import java.io.File
import java.util

@EventBusSubscriber
object LayerManagerServer extends LayerManager[CCWorldServer]