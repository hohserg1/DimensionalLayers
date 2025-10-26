package hohserg.dimensional.layers.preset.spec

import net.minecraft.block.state.IBlockState
import net.minecraft.potion.Potion

sealed trait AdditionalFeature {

}

case class BlockReplacing(from: IBlockState, to: IBlockState) extends AdditionalFeature

case class PotionEffectGranting(effect: Potion, amplifier: Int, playerOnly: Boolean) extends AdditionalFeature
