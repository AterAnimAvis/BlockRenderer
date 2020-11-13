package com.unascribed.blockrenderer.fabric.client.screens.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.blockrenderer.fabric.client.varia.rendering.Display;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class HoverableTextFieldWidget extends EditBox {

    private final Screen owner;
    private final Supplier<List<Component>> tooltip;

    public HoverableTextFieldWidget(Screen owner, Font font, int x, int y, int w, int h, Component title, Component tooltip) {
        this(owner, font, x, y, w, h, title, () -> Collections.singletonList(tooltip));
    }

    public HoverableTextFieldWidget(Screen owner, Font font, int x, int y, int w, int h, Component title, Supplier<List<Component>> tooltip) {
        super(font, x, y, w, h, title);
        this.owner = owner;
        this.tooltip = tooltip;
    }

    @Override
    public void renderToolTip(PoseStack stack, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY))
            Display.renderTooltip(owner, stack, tooltip.get(), mouseX, mouseY);
    }
}
