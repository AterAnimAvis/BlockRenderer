package com.unascribed.blockrenderer.fabric.client.screens.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.blockrenderer.fabric.client.varia.rendering.Display;
import com.unascribed.blockrenderer.fabric.client.varia.rendering.GL;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class HoverableTinyButtonWidget extends ButtonWidget {

    private final Screen owner;
    private final Supplier<List<Text>> tooltip;

    public HoverableTinyButtonWidget(Screen owner, int x, int y, Text message, Text tooltip, PressAction onPress) {
        this(owner, x, y, message, () -> Collections.singletonList(tooltip), onPress);
    }

    public HoverableTinyButtonWidget(Screen owner, int x, int y, Text message, Supplier<List<Text>> tooltip, PressAction onPress) {
        super(x, y, 20, 20, message, onPress);
        this.tooltip = tooltip;
        this.owner = owner;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        int i = this.getYImage(this.isHovered());
        MinecraftClient minecraft = MinecraftClient.getInstance();
        minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);

        GL.color(1.0F, 1.0F, 1.0F, alpha);
        GL.enableDefaultBlend();
        GL.blendFunction(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

        drawTexture(stack, x, y, 0, 46 + i * 20, width / 2, height);
        drawTexture(stack, x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height);

        renderBg(stack, minecraft, mouseX, mouseY);
    }

    @Override
    public void renderToolTip(MatrixStack stack, int mouseX, int mouseY) {
        Display.renderTooltip(owner, stack, tooltip.get(), mouseX, mouseY);
    }

}
