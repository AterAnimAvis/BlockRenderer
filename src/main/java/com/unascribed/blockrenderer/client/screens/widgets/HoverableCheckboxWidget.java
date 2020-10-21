package com.unascribed.blockrenderer.client.screens.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.client.varia.rendering.Display;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class HoverableCheckboxWidget extends CheckboxButton {

    private final Screen owner;
    private final Supplier<List<ITextComponent>> tooltip;

    public HoverableCheckboxWidget(Screen owner, int x, int y, int width, int height, ITextComponent text, ITextComponent tooltip, boolean checked) {
        this(owner, x, y, width, height, text, () -> Collections.singletonList(tooltip), checked);
    }

    public HoverableCheckboxWidget(Screen owner, int i, int j, int k, int l, ITextComponent text, Supplier<List<ITextComponent>> tooltip, boolean checked) {
        super(i, j, k, l, text, checked);
        this.tooltip = tooltip;
        this.owner = owner;
    }

    @Override
    public void renderToolTip(MatrixStack stack, int mouseX, int mouseY) {
        Display.renderTooltip(owner, stack, tooltip.get(), mouseX, mouseY);
    }

}
