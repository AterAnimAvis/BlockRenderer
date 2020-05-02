package com.unascribed.blockrenderer.screens.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.blockrenderer.utils.Rendering;
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
        super(x, y, 20, 20,  message, onPress);
        this.tooltip = tooltip;
        this.owner = owner;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int i = this.getYImage(this.isHovered());

        MinecraftClient client = MinecraftClient.getInstance();
        client.getTextureManager().bindTexture(WIDGETS_LOCATION);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

        drawTexture(matrices, x, y, 0, 46 + i * 20, width / 2, height);
        drawTexture(matrices, x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height);

        renderBg(matrices, client, mouseX, mouseY);
    }

    @Override
    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        Rendering.drawHoveringText(owner, matrixStack, tooltip.get(), mouseX, mouseY);
    }

}
