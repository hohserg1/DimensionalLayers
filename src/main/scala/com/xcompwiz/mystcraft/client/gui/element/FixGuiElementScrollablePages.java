package com.xcompwiz.mystcraft.client.gui.element;

import gloomyfolken.hooklib.api.*;
import hohserg.dimensional.layers.gui.settings.mystcraft.SelectedSymbol;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@HookContainer
public class FixGuiElementScrollablePages {

    @FieldLens(createField = true)
    public static FieldAccessor<GuiElementScrollablePages, Boolean> dimensionalLayersMode = FieldAccessor.defaultValue(false);


    @Hook
    @OnExpression(expressionPattern = "accessingToClientPlayerInv", shift = Shift.INSTEAD)
    public static boolean _onMouseDown(GuiElementScrollablePages self, int mouseX, int mouseY, int button) {
        if (dimensionalLayersMode.get(self))
            return SelectedSymbol.get().isEmpty();
        else
            return accessingToClientPlayerInv(self);
    }


    public static boolean accessingToClientPlayerInv(GuiElementScrollablePages self) {
        return self.mc.player.inventory.getItemStack().isEmpty();
    }
}
