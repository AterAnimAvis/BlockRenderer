package com.unascribed.blockrenderer.fabric.client.screens.widgets;

import com.unascribed.blockrenderer.fabric.client.varia.rendering.Display;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class HoverableTextFieldWidget extends TextFieldWidget {

    private final Screen owner;
    private final Supplier<List<Text>> tooltip;

    public HoverableTextFieldWidget(Screen owner, TextRenderer font, int x, int y, int w, int h, Text title, Text tooltip) {
        this(owner, font, x, y, w, h, title, () -> Collections.singletonList(tooltip));
    }

    public HoverableTextFieldWidget(Screen owner, TextRenderer font, int x, int y, int w, int h, Text title, Supplier<List<Text>> tooltip) {
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
