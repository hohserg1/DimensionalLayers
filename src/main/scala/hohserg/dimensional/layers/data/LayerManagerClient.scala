package hohserg.dimensional.layers.data

import hohserg.dimensional.layers.CCWorldClient
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.relauncher.Side

@EventBusSubscriber(value = Array(Side.CLIENT))
object LayerManagerClient extends LayerManager[CCWorldClient]
