package com.unascribed.blockrenderer.fabric.client.varia.rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.unascribed.blockrenderer.varia.rendering.DisplayI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

import static com.unascribed.blockrenderer.Interop.GL;
import static com.unascribed.blockrenderer.fabric.client.varia.Strings.rawText;

public class Display implements DisplayI<Component> {

    public static final Display INSTANCE = new Display();

    private static final Minecraft client = Minecraft.getInstance();
    private static final Screen helper = new Screen(rawText("DUMMY")) {

    };

    @Override
    public void drawRect(int x1, int y1, int x2, int y2, int color) {
        drawRect(new PoseStack(), x1, y1, x2, y2, color);
    }

    public static void drawRect(PoseStack stack, int x1, int y1, int x2, int y2, int color) {
        GuiComponent.fill(stack, x1, y1, x2, y2, color);
    }

    @Override
    public void drawCenteredString(Component component, int x, int y, int color) {
        drawCenteredString(new PoseStack(), component, x, y, color);
    }

    public static void drawCenteredString(PoseStack stack, Component component, int x, int y, int color) {
        drawCenteredString(stack, component.getString(), x, y, color);
    }

    public static void drawCenteredString(PoseStack stack, String str, int x, int y, int color) {
        client.font.drawShadow(stack, str, x - client.font.width(str) / 2F, y, color);
    }

    public static void renderTooltip(Screen owner, PoseStack stack, List<Component> tooltip, int x, int y) {
        helper.init(client, owner.width, owner.height);
        helper.renderComponentTooltip(stack, tooltip, x, y);
    }

    @Override
    public void drawDirtBackground(int scaledWidth, int scaledHeight) {
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
