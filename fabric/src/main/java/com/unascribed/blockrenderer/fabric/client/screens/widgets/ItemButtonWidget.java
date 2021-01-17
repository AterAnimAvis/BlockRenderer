package com.unascribed.blockrenderer.fabric.client.screens.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.fabric.client.varia.Registries;
import com.unascribed.blockrenderer.fabric.client.varia.rendering.Display;
import com.unascribed.blockrenderer.fabric.client.varia.rendering.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ItemButtonWidget extends Button {

    public static final ResourceLocation CUSTOM_WIDGETS = new ResourceLocation("block_renderer:textures/gui/widgets.png");

    private final Screen owner;
    private final Supplier<List<ITextComponent>> tooltip;
    private final ItemRenderer renderer;
    private final Supplier<ItemStack> stack;
    private final boolean isMap;

    public ItemButtonWidget(Screen owner, ItemRenderer renderer, Supplier<ItemStack> stack, int x, int y, ITextComponent message, ITextComponent tooltip, IPressable onPress) {
        this(owner, renderer, stack, x, y, message, () -> Collections.singletonList(tooltip), onPress);
    }

    public ItemButtonWidget(Screen owner, ItemRenderer renderer, Supplier<ItemStack> stack, int x, int y, ITextComponent message, Supplier<List<ITextComponent>> tooltip, IPressable onPress) {
        super(x - 32, y - 32, 64, 64, message, onPress);
        this.tooltip = tooltip;
        this.owner = owner;
        this.renderer = renderer;
        this.stack = stack;
        this.isMap = stack.get().getItem() == Registries.MAP.getValue();
    }

    @Override
    public void renderButton(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {

        if (isHovered() && active)
            Display.drawRect(matrix, x, y, x + width, y + height, 0x33FFFFFF);

        renderItemStack(stack.get(), x, y, 4.0f);

        if (isHovered())
            renderToolTip(matrix, mouseX, mouseY);

        GL.pushMatrix();
        GL.translate(0, 0, 64.0f);

        if (!active) {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bindTexture(CUSTOM_WIDGETS);

            blit(matrix, x + 44, y + 44, 22, 0, 20, 20);

            minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
        }

        GL.popMatrix();
    }

    private void renderItemStack(ItemStack stack, int x, int y, float scale) {
        int BASE_Z_LEVEL = 100;

        GL.pushMatrix();

        // Maps need more centering
        if (isMap) GL.translate(-2f, 0f, 0f);

        GL.translate(x, y, 32.0f);
        GL.scaleFixedZLevel(scale, -BASE_Z_LEVEL);

        renderer.zLevel = -BASE_Z_LEVEL / 2f;
        renderer.renderItemAndEffectIntoGUI(stack, 0, 0);
        renderer.zLevel = 0.0F;
        GL.popMatrix();

    }

    @Override
    public void renderToolTip(MatrixStack stack, int mouseX, int mouseY) {
        Display.renderTooltip(owner, stack, tooltip.get(), mouseX, mouseY);
    }

}
