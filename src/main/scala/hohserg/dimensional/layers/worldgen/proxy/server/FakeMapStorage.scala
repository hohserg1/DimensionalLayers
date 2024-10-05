package hohserg.dimensional.layers.worldgen.proxy.server

import net.minecraft.world.storage.{ISaveHandler, MapStorage}

class FakeMapStorage(saveHandler: ISaveHandler) extends MapStorage(saveHandler) {
}
