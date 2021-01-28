package com.unascribed.blockrenderer.fabric.client.varia.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.InternalAPI;
import com.unascribed.blockrenderer.varia.rendering.DisplayI;
import com.unascribed.blockrenderer.varia.rendering.GLI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

import static com.unascribed.blockrenderer.fabric.client.varia.Strings.rawText;

public class Display implements DisplayI<ITextComponent> {

    public static final Display INSTANCE = new Display();

    private static final GLI GL = InternalAPI.getGL();
    private static final Minecraft client = Minecraft.getInstance();
    private static final Screen helper = new Screen(rawText("DUMMY")) {

    };

    @Override
    public void drawRect(int x1, int y1, int x2, int y2, int color) {
        drawRect(new MatrixStack(), x1, y1, x2, y2, color);
    }

    public static void drawRect(MatrixStack stack, int x1, int y1, int x2, int y2, int color) {
        AbstractGui.fill(stack, x1, y1, x2, y2, color);
    }

    @Override
    public void drawCenteredString(ITextComponent component, int x, int y, int color) {
        drawCenteredString(new MatrixStack(), component, x, y, color);
    }

    public static void drawCenteredString(MatrixStack stack, ITextComponent component, int x, int y, int color) {
        drawCenteredString(stack, component.getString(), x, y, color);
    }

    public static void drawCenteredString(MatrixStack stack, String str, int x, int y, int color) {
        client.fontRenderer.drawStringWithShadow(stack, str, x - client.fontRenderer.getStringWidth(str) / 2F, y, color);
    }

    public static void renderTooltip(Screen owner, MatrixStack stack, List<ITextComponent> tooltip, int x, int y) {
        helper.init(client, owner.width, owner.height);
        helper.func_243308_b(stack, tooltip, x, y);
    }

    @Override
    public void drawDirtBackground(int scaledWidth, int scaledHeight, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        client.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);

        GL.color(1.0F, 1.0F, 1.0F, alpha);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);

        // 0 h
        bufferbuilder.pos(0.0D, scaledHeight, 0.0D)
                .color(64, 64, 64, 255)
                .tex(0.0F, scaledHeight / 32.0F + 0.0F)
                .endVertex();
        // w h
        bufferbuilder.pos(scaledWidth, scaledHeight, 0.0D)
                .color(64, 64, 64, 255)
                .tex(scaledWidth / 32.0F, scaledHeight / 32.0F + 0.0F)
                .endVertex();
        // w 0
        bufferbuilder.pos(scaledWidth, 0.0D, 0.0D)
                .color(64, 64, 64, 255)
                .tex(scaledWidth / 32.0F, 0.0F)
                .endVertex();
        // 0 0
        bufferbuilder.pos(0.0D, 0.0D, 0.0D)
                .color(64, 64, 64, 255)
                .tex(0.0F, 0.0F)
                .endVertex();

        tessellator.draw();
    }

}
