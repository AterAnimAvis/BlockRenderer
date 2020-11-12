package com.unascribed.blockrenderer.fabric.client.varia.rendering;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.List;

public interface Display {

    MinecraftClient client = MinecraftClient.getInstance();
    TextRenderer font = client.textRenderer;
    Screen helper = new Screen(new LiteralText("DUMMY")) {

    };

    static void drawRect(MatrixStack stack, int x1, int y1, int x2, int y2, int color) {
        DrawableHelper.fill(stack, x1, y1, x2, y2, color);
    }

    static void drawCenteredString(MatrixStack stack, Text component, int x, int y, int color) {
        drawCenteredString(stack, component.getString(), x, y, color);
    }

    static void drawCenteredString(MatrixStack stack, String str, int x, int y, int color) {
        font.drawWithShadow(stack, str, x - font.getWidth(str) / 2F, y, color);
    }

    static void renderTooltip(Screen owner, MatrixStack stack, List<Text> tooltip, int x, int y) {
        helper.init(client, owner.width, owner.height);
        helper.renderTooltip(stack, tooltip, x, y);
    }

    static void drawDirtBackground(int scaledWidth, int scaledHeight) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);

        GL.color(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);

        // 0 h
        bufferbuilder.vertex(0.0D, scaledHeight, 0.0D)
                .color(64, 64, 64, 255)
                .texture(0.0F, scaledHeight / 32.0F + 0.0F)
                .next();
        // w h
        bufferbuilder.vertex(scaledWidth, scaledHeight, 0.0D)
                .color(64, 64, 64, 255)
                .texture(scaledWidth / 32.0F, scaledHeight / 32.0F + 0.0F)
                .next();
        // w 0
        bufferbuilder.vertex(scaledWidth, 0.0D, 0.0D)
                .color(64, 64, 64, 255)
                .texture(scaledWidth / 32.0F, 0.0F)
                .next();
        // 0 0
        bufferbuilder.vertex(0.0D, 0.0D, 0.0D)
                .color(64, 64, 64, 255)
                .texture(0.0F, 0.0F)
                .next();

        tessellator.draw();
    }

}
