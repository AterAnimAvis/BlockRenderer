package com.unascribed.blockrenderer.fabric.client.screens.widgets;

import com.unascribed.blockrenderer.fabric.client.varia.rendering.Display;
import com.unascribed.blockrenderer.fabric.client.varia.rendering.GL;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ItemButtonMultiWidget extends ButtonWidget {

    private final Screen owner;
    private final Function<Integer, List<Text>> tooltip;
    private final ItemRenderer renderer;
    private final Function<Integer, ItemStack> stack;
    public int state = 0;

    public ItemButtonMultiWidget(Screen owner, ItemRenderer renderer, Function<Integer, ItemStack> stack, int x, int y, Text message, Text tooltip, PressAction onPress) {
        this(owner, renderer, stack, x, y, message, (state) -> Collections.singletonList(tooltip), onPress);
    }

    public ItemButtonMultiWidget(Screen owner, ItemRenderer renderer, Function<Integer, ItemStack> stack, int x, int y, Text message, Function<Integer, List<Text>> tooltip, PressAction onPress) {
        super(x, y, 20, 20, message, onPress);
        this.tooltip = tooltip;
        this.owner = owner;
        this.renderer = renderer;
        this.stack = stack;
    }

    @Override
    public void renderButton(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {

        if (isHovered() && active)
            Display.drawRect(matrix, x, y, x + width, y + height, 0x33FFFFFF);

        renderItemStack(stack.apply(state), x, y, 1.25f);

        if (isHovered())
            renderToolTip(matrix, mouseX, mouseY);
    }

    private void renderItemStack(ItemStack stack, int x, int y, float scale) {
        int BASE_Z_LEVEL = 100;

        GL.pushMatrix();

        GL.translate(x, y, 32.0f);
        GL.scaleFixedZLevel(scale, -BASE_Z_LEVEL);

        renderer.zOffset = -BASE_Z_LEVEL / 2f;
        renderer.renderInGuiWithOverrides(stack, 0, 0);
        renderer.zOffset = 0.0F;
        GL.popMatrix();

    }

    @Override
    public void renderToolTip(MatrixStack stack, int mouseX, int mouseY) {
        Display.renderTooltip(owner, stack, tooltip.apply(state), mouseX, mouseY);
    }

}
