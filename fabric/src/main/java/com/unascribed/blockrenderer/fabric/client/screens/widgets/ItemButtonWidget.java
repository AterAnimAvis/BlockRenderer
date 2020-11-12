package com.unascribed.blockrenderer.fabric.client.screens.widgets;

import com.unascribed.blockrenderer.fabric.client.varia.Registries;
import com.unascribed.blockrenderer.fabric.client.varia.rendering.Display;
import com.unascribed.blockrenderer.fabric.client.varia.rendering.GL;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ItemButtonWidget extends ButtonWidget {

    public static final Identifier CUSTOM_WIDGETS = new Identifier("block_renderer:textures/gui/widgets.png");

    private final Screen owner;
    private final Supplier<List<Text>> tooltip;
    private final ItemRenderer renderer;
    private final Supplier<ItemStack> stack;
    private final boolean isMap;

    public ItemButtonWidget(Screen owner, ItemRenderer renderer, Supplier<ItemStack> stack, int x, int y, Text message, Text tooltip, PressAction onPress) {
        this(owner, renderer, stack, x, y, message, () -> Collections.singletonList(tooltip), onPress);
    }

    public ItemButtonWidget(Screen owner, ItemRenderer renderer, Supplier<ItemStack> stack, int x, int y, Text message, Supplier<List<Text>> tooltip, PressAction onPress) {
        super(x - 32, y - 32, 64, 64, message, onPress);
        this.tooltip = tooltip;
        this.owner = owner;
        this.renderer = renderer;
        this.stack = stack;
        this.isMap = stack.get().getItem() == Registries.MAP.get();
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
            MinecraftClient minecraft = MinecraftClient.getInstance();
            minecraft.getTextureManager().bindTexture(CUSTOM_WIDGETS);

            drawTexture(matrix, x + 44, y + 44, 22, 0, 20, 20);

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

        renderer.zOffset = -BASE_Z_LEVEL / 2f;
        renderer.renderInGuiWithOverrides(stack, 0, 0);
        renderer.zOffset = 0.0F;
        GL.popMatrix();

    }

    @Override
    public void renderToolTip(MatrixStack stack, int mouseX, int mouseY) {
        Display.renderTooltip(owner, stack, tooltip.get(), mouseX, mouseY);
    }

}
