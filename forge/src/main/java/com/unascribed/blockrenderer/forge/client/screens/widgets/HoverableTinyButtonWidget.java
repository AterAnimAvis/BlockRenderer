package com.unascribed.blockrenderer.forge.client.screens.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.blockrenderer.forge.client.varia.rendering.Display;
import com.unascribed.blockrenderer.forge.client.varia.rendering.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class HoverableTinyButtonWidget extends Button {

    private final Screen owner;
    private final Supplier<List<ITextComponent>> tooltip;

    public HoverableTinyButtonWidget(Screen owner, int x, int y, ITextComponent message, ITextComponent tooltip, IPressable onPress) {
        this(owner, x, y, message, () -> Collections.singletonList(tooltip), onPress);
    }

    public HoverableTinyButtonWidget(Screen owner, int x, int y, ITextComponent message, Supplier<List<ITextComponent>> tooltip, IPressable onPress) {
        super(x, y, 20, 20, message, onPress);
        this.tooltip = tooltip;
        this.owner = owner;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        int i = this.getYImage(this.isHovered());
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);

        GL.color(1.0F, 1.0F, 1.0F, alpha);
        GL.enableDefaultBlend();
        GL.blendFunction(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        blit(stack, x, y, 0, 46 + i * 20, width / 2, height);
        blit(stack, x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height);

        renderBg(stack, minecraft, mouseX, mouseY);
    }

    @Override
    public void renderToolTip(MatrixStack stack, int mouseX, int mouseY) {
        Display.renderTooltip(owner, stack, tooltip.get(), mouseX, mouseY);
    }

}
