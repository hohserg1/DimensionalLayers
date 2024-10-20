package hohserg.dimensional.layers.gui

import net.minecraft.client.gui.{GuiButton, GuiConfirmOpenLink}

import java.net.URI

class GuiAcceptOpenLink(parent: GuiBase, link: String) extends GuiConfirmOpenLink(parent, link, 0, true) {
  disableSecurityWarning()

  override def actionPerformed(button: GuiButton): Unit = {
    button.id match {
      case 0 => parent.openWebLink(new URI(link))
      case 1 => copyLinkToClipboard()
      case _ =>
    }
    mc.displayGuiScreen(parent)
  }

}
