package com.unascribed.blockrenderer.fabric.client.screens.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.fabric.client.varia.rendering.Display;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class HoverableTextFieldWidget extends TextFieldWidget {

    private final Screen owner;
    private final Supplier<List<ITextComponent>> tooltip;

    public HoverableTextFieldWidget(Screen owner, FontRenderer font, int x, int y, int w, int h, ITextComponent title, ITextComponent tooltip) {
        this(owner, font, x, y, w, h, title, () -> Collections.singletonList(tooltip));
    }

    public HoverableTextFieldWidget(Screen owner, FontRenderer font, int x, int y, int w, int h, ITextComponent title, Supplier<List<ITextComponent>> tooltip) {
        super(font, x, y, w, h, title);
        this.owner = owner;
        this.tooltip = tooltip;
    }

    @Override
    public void renderToolTip(MatrixStack stack, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY))
            Display.renderTooltip(owner, stack, tooltip.get(), mouseX, mouseY);
    }
}
