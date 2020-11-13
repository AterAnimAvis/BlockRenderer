package com.unascribed.blockrenderer.fabric.client.screens.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.blockrenderer.fabric.client.varia.rendering.Display;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class HoverableCheckboxWidget extends Checkbox {

    private final Screen owner;
    private final Supplier<List<Component>> tooltip;

    public HoverableCheckboxWidget(Screen owner, int x, int y, int width, int height, Component text, Component tooltip, boolean checked) {
        this(owner, x, y, width, height, text, () -> Collections.singletonList(tooltip), checked);
    }

    public HoverableCheckboxWidget(Screen owner, int i, int j, int k, int l, Component text, Supplier<List<Component>> tooltip, boolean checked) {
        super(i, j, k, l, text, checked);
        this.tooltip = tooltip;
        this.owner = owner;
    }

    @Override
    public void renderToolTip(PoseStack stack, int mouseX, int mouseY) {
        Display.renderTooltip(owner, stack, tooltip.get(), mouseX, mouseY);
    }

}
