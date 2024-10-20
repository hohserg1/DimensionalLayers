package hohserg.dimensional.layers.data.layer.base

import hohserg.dimensional.layers.preset.spec.LayerSpec
import hohserg.dimensional.layers.worldgen.proxy.client.ProxyWorldClient
import hohserg.dimensional.layers.{CCWorld, CCWorldClient, CCWorldServer, Main}
import net.minecraft.world.DimensionType
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import java.io.File
import scala.util.{Failure, Success, Try}

trait Layer {
  type Spec <: LayerSpec
  type Bounds <: LayerBounds

  type G <: Generator

  def bounds: Bounds

  def spec: Spec

  def originalWorld: CCWorld

  protected def createGenerator(original: CCWorldServer): G

  protected def fakeSaveFolderName: String = "__nondimensional_"

  protected def createFakeSaveFolder(original: CCWorldServer): File = {
    val r = new File(
      new File(original.getSaveHandler.getWorldDirectory, Main.modid + "/fake_save_handler/"),
      original.worldInfo.getWorldName + "__" + fakeSaveFolderName + "_" + bounds.realStartCubeY
    )
    r.mkdirs()
    r
  }

  lazy val fakeSaveLocation: File = originalWorld match {
    case serverWorld: CCWorldServer => createFakeSaveFolder(serverWorld)
  }

  lazy val generator: G = originalWorld match {
    case serverWorld: CCWorldServer => createGenerator(serverWorld)
  }

}

trait DimensionalLayer extends Layer {
  override type Bounds = DimensionalLayerBounds
  override type G <: DimensionalGenerator

  def dimensionType: DimensionType

  def isCubic: Boolean

  override protected def fakeSaveFolderName: String = dimensionType.getName

  @SideOnly(Side.CLIENT)
  protected def createClientProxyWorld(original: CCWorldClient): ProxyWorldClient = ProxyWorldClient(original, this)

  @SideOnly(Side.CLIENT)
  lazy val clientProxyWorld: ProxyWorldClient = originalWorld match {
    case clientWorld: CCWorldClient =>
      Try(createClientProxyWorld(clientWorld)) match {
        case Failure(exception) =>
          Main.sided.printError("failed to create client proxy world", (dimensionType, dimensionType.getId, dimensionType.clazz, dimensionType.getName, dimensionType.suffix, this).toString, exception)
          throw exception

        case Success(value) =>
          value
      }
  }
}