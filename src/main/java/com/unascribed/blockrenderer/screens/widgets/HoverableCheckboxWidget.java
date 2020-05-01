package com.unascribed.blockrenderer.screens.widgets;

import com.unascribed.blockrenderer.utils.Rendering;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class HoverableCheckboxWidget extends CheckboxWidget {

    private final Screen owner;
    private final Supplier<List<Text>> tooltip;

    public HoverableCheckboxWidget(Screen owner, int x, int y, int width, int height, Text text, Text tooltip, boolean checked) {
        this(owner, x, y, width, height, text, () -> Collections.singletonList(tooltip), checked);
    }

    public HoverableCheckboxWidget(Screen owner, int x, int y, int width, int height, Text text, Supplier<List<Text>> tooltip, boolean checked) {
        this(owner, x, y, width, height, text, tooltip, checked, true);
    }

    public HoverableCheckboxWidget(Screen owner, int i, int j, int k, int l, Text text, Supplier<List<Text>> tooltip, boolean checked, boolean renderText) {
        super(i, j, k, l, text, checked, renderText);
        this.tooltip = tooltip;
        this.owner = owner;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        Rendering.drawHoveringText(owner, matrixStack, tooltip.get(), mouseX, mouseY);
    }

}
