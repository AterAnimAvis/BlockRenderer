package com.unascribed.blockrenderer.forge.client.screens.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.InternalAPI;
import com.unascribed.blockrenderer.forge.client.varia.rendering.Display;
import com.unascribed.blockrenderer.varia.rendering.GLI;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ItemButtonMultiWidget extends Button {

    private final GLI GL = InternalAPI.getGL();
    private final Screen owner;
    private final Function<Integer, List<ITextComponent>> tooltip;
    private final ItemRenderer renderer;
    private final Function<Integer, ItemStack> stack;
    public int state = 0;

    public ItemButtonMultiWidget(Screen owner, ItemRenderer renderer, Function<Integer, ItemStack> stack, int x, int y, ITextComponent message, ITextComponent tooltip, IPressable onPress) {
        this(owner, renderer, stack, x, y, message, (state) -> Collections.singletonList(tooltip), onPress);
    }

    public ItemButtonMultiWidget(Screen owner, ItemRenderer renderer, Function<Integer, ItemStack> stack, int x, int y, ITextComponent message, Function<Integer, List<ITextComponent>> tooltip, IPressable onPress) {
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
        int BASE_BLIT_OFFSET = 100;

        GL.pushMatrix();

        GL.translate(x, y, 32.0f);
        GL.scaleFixedZLevel(scale, -BASE_BLIT_OFFSET);

        renderer.blitOffset = -BASE_BLIT_OFFSET / 2f;
        renderer.renderAndDecorateItem(stack, 0, 0);
        renderer.blitOffset = 0.0F;
        GL.popMatrix();

    }

    @Override
    public void renderToolTip(MatrixStack stack, int mouseX, int mouseY) {
        Display.renderTooltip(owner, stack, tooltip.apply(state), mouseX, mouseY);
    }

}
