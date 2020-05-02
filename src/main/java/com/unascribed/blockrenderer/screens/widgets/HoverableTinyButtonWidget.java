package com.unascribed.blockrenderer.screens.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.blockrenderer.utils.Rendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class HoverableTinyButtonWidget extends Button {

    private final Screen owner;
    private final Supplier<List<String>> tooltip;

    public HoverableTinyButtonWidget(Screen owner, int x, int y, String message, String tooltip, IPressable onPress) {
        this(owner, x, y, message, () -> Collections.singletonList(tooltip), onPress);
    }

    public HoverableTinyButtonWidget(Screen owner, int x, int y, String message, Supplier<List<String>> tooltip, IPressable onPress) {
        super(x, y, 20, 20,  message, onPress);
        this.tooltip = tooltip;
        this.owner = owner;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        int i = this.getYImage(this.isHovered());
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        blit(x, y, 0, 46 + i * 20, width / 2, height);
        blit(x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height);

        renderBg(minecraft, mouseX, mouseY);
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        Rendering.drawHoveringText(owner, tooltip.get(), mouseX, mouseY);
    }

}
