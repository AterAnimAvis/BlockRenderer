package com.unascribed.blockrenderer.screens.widgets;

import com.unascribed.blockrenderer.utils.Rendering;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.CheckboxButton;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class HoverableCheckboxWidget extends CheckboxButton {

    private final Screen owner;
    private final Supplier<List<String>> tooltip;

    public HoverableCheckboxWidget(Screen owner, int x, int y, int width, int height, String text, String tooltip, boolean checked) {
        this(owner, x, y, width, height, text, () -> Collections.singletonList(tooltip), checked);
    }

    public HoverableCheckboxWidget(Screen owner, int i, int j, int k, int l, String text, Supplier<List<String>> tooltip, boolean checked) {
        super(i, j, k, l, text, checked);
        this.tooltip = tooltip;
        this.owner = owner;
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        Rendering.drawHoveringText(owner, tooltip.get(), mouseX, mouseY);
    }

}
