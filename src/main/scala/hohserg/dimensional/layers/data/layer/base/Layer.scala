package hohserg.dimensional.layers.data.layer.base

import hohserg.dimensional.layers.lens.{DimensionTypeLens, WorldLens}
import hohserg.dimensional.layers.preset.spec.*
import hohserg.dimensional.layers.worldgen.proxy.client.ProxyWorldClient
import hohserg.dimensional.layers.{CCWorld, CCWorldClient, CCWorldServer, Main}
import net.minecraft.block.state.IBlockState
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

  lazy val hasPotionEffectGranting: Boolean = spec.additionalFeatures.exists(_.isInstanceOf[PotionEffectGranting])

  protected def createGenerator(original: CCWorldServer): G

  protected def fakeSaveFolderName: String = "__nondimensional_"

  protected def createFakeSaveFolder(original: CCWorldServer): File = {
    val r = new File(
      new File(original.getSaveHandler.getWorldDirectory, Main.modid + "/fake_save_handler/"),
      WorldLens.worldInfo.get(original).getWorldName + "__" + fakeSaveFolderName + "_" + bounds.realStartCubeY
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
  
  def dimensionId = dimensionType.getId

  lazy val blockReplacements: Map[IBlockState, IBlockState] = spec.additionalFeatures.collect { case BlockReplacing(from, to) => from -> to }.toMap

  lazy val hasBlockReplacing: Boolean = blockReplacements.nonEmpty

  override protected def fakeSaveFolderName: String = dimensionType.getName

  @SideOnly(Side.CLIENT)
  protected def createClientProxyWorld(original: CCWorldClient): ProxyWorldClient = {
    new ProxyWorldClient(original, this)
  }

  @SideOnly(Side.CLIENT)
  lazy val clientProxyWorld: ProxyWorldClient = originalWorld match {
    case clientWorld: CCWorldClient =>
      Try(createClientProxyWorld(clientWorld)) match {
        case Failure(exception) =>
          Main.sided.printError("failed to create client proxy world", (dimensionType, dimensionType.getId, DimensionTypeLens.clazz.get(dimensionType), dimensionType.getName, DimensionTypeLens.suffix.get(dimensionType), this).toString, exception)
          throw exception

        case Success(value) =>
          value
      }
  }
}

abstract class VanillaDimensionLayerBase(_realStartCubeY: Int, originalWorld: CCWorld, offsets: CubeOffsets) extends DimensionalLayer {
  override val bounds: DimensionalLayerBounds = new DimensionalLayerBounds {
    override val realStartCubeY: Int = _realStartCubeY
    override val cubeHeight: Int = offsets.height
    override val virtualStartCubeY: Int = offsets.bottomOffset
    override val virtualEndCubeY: Int = 16 - offsets.topOffset - 1
  }

  override def isCubic: Boolean = false

}