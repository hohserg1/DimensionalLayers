package hohserg.dimensional.layers.data.layer.base

import hohserg.dimensional.layers.worldgen.proxy.server.ProxyWorldServer

trait DimensionalGenerator extends Generator {

  def proxyWorld: ProxyWorldServer

}
