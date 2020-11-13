package com.unascribed.blockrenderer.fabric.client.varia.rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.List;

public interface Display {

    Minecraft client = Minecraft.getInstance();
    Font font = client.font;
    Screen helper = new Screen(new TextComponent("DUMMY")) {

    };

    static void drawRect(PoseStack stack, int x1, int y1, int x2, int y2, int color) {
        GuiComponent.fill(stack, x1, y1, x2, y2, color);
    }

    static void drawCenteredString(PoseStack stack, Component component, int x, int y, int color) {
        drawCenteredString(stack, component.getString(), x, y, color);
    }

    static void drawCenteredString(PoseStack stack, String str, int x, int y, int color) {
        font.drawShadow(stack, str, x - font.width(str) / 2F, y, color);
    }

    static void renderTooltip(Screen owner, PoseStack stack, List<Component> tooltip, int x, int y) {
        helper.init(client, owner.width, owner.height);
        helper.renderComponentTooltip(stack, tooltip, x, y);
    }

    static void drawDirtBackground(int scaledWidth, int scaledHeight) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();

        client.getTextureManager().bind(GuiComponent.BACKGROUND_LOCATION);

        GL.color(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(7, DefaultVertexFormat.POSITION_COLOR_TEX);

        // 0 h
        bufferbuilder.vertex(0.0D, scaledHeight, 0.0D)
                .color(64, 64, 64, 255)
                .uv(0.0F, scaledHeight / 32.0F + 0.0F)
                .endVertex();
        // w h
        bufferbuilder.vertex(scaledWidth, scaledHeight, 0.0D)
                .color(64, 64, 64, 255)
                .uv(scaledWidth / 32.0F, scaledHeight / 32.0F + 0.0F)
                .endVertex();
        // w 0
        bufferbuilder.vertex(scaledWidth, 0.0D, 0.0D)
                .color(64, 64, 64, 255)
                .uv(scaledWidth / 32.0F, 0.0F)
                .endVertex();
        // 0 0
        bufferbuilder.vertex(0.0D, 0.0D, 0.0D)
                .color(64, 64, 64, 255)
                .uv(0.0F, 0.0F)
                .endVertex();

        tessellator.end();
    }

}
