package hohserg.dimensional.layers.gui.settings.additional

import hohserg.dimensional.layers.gui.GuiBaseSettings.ValueHolder
import hohserg.dimensional.layers.gui.RelativeCoord.{alignLeft, alignTop}
import hohserg.dimensional.layers.gui.{DrawableArea, GuiBase, GuiTexturedButton, IconUtils, RelativeCoord}
import hohserg.dimensional.layers.preset.spec.AdditionalFeature

import java.awt.Rectangle

package object features {

  def addAdditionalFeaturesWidgets(holder: ValueHolder[Seq[AdditionalFeature]], top: Int, bottom: Int)(implicit gui: GuiBase): Unit = {
    val addFeatListW = IconUtils.width
    gui.addCenteredLabel("additional", alignLeft(10 + addFeatListW / 2), alignTop(top), 0xffffffff)
    gui.addCenteredLabel("features:", alignLeft(10 + addFeatListW / 2), alignTop(top + 10), 0xffffffff)
    gui.addElement(new AdditionalFeaturesList(10, top + 20, addFeatListW, bottom - top - 20 - 15, holder))
    gui.addButton(new GuiTexturedButton(10, bottom - 15, addFeatListW, 15, "+", new Rectangle(2, 131, 64, 15))(gui.show(new GuiNewAdditionalFeature(_, holder))))
  }

  val arrowUV = new Rectangle(33, 79, 6, 5)
  val arrowArea = DrawableArea(
    RelativeCoord.horizontalCenterMin(6).shift(-6), RelativeCoord.verticalCenterMin(5),
    RelativeCoord.horizontalCenterMax(6).shift(-6), RelativeCoord.verticalCenterMax(5),
    arrowUV, arrowUV
  )
  val largeArrowArea = DrawableArea(
    RelativeCoord.horizontalCenterMin(12), RelativeCoord.verticalCenterMin(10),
    RelativeCoord.horizontalCenterMax(12), RelativeCoord.verticalCenterMax(10),
    arrowUV, arrowUV
  )
  val remove = DrawableArea(
    RelativeCoord.alignRight(-1 - 13), RelativeCoord.verticalCenterMin(12),
    RelativeCoord.alignRight(-1), RelativeCoord.verticalCenterMax(12),
    new Rectangle(2, 38, 13, 12)
  )

}
