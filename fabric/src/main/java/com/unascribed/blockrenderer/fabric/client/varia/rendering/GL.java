package com.unascribed.blockrenderer.fabric.client.varia.rendering;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.blockrenderer.varia.rendering.GLI;
import net.minecraft.client.Minecraft;
import org.intellij.lang.annotations.MagicConstant;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

@SuppressWarnings("deprecation")
public class GL implements GLI {

    public static final GLI INSTANCE = new GL();

    private static final Minecraft client = Minecraft.getInstance();
    private static final Window window = client.getWindow();

    /* ================================================================================================== Matrix ==== */

    @Override
    public void pushMatrix() {
        RenderSystem.pushMatrix();
    }

    @Override
    public void popMatrix() {
        RenderSystem.popMatrix();
    }

    @Override
    public void loadIdentity() {
        RenderSystem.loadIdentity();
    }

    /* ========================================================================================= Transformations ==== */

    @Override
    public void translate(float x, float y, float z) {
        RenderSystem.translatef(x, y, z);
    }

    @Override
    public void scale(float x, float y, float z) {
        RenderSystem.scalef(x, y, z);
    }

    /* ================================================================================================ Lighting ==== */

    @Override
    public void setupItemStackLighting() {
        Lighting.setupFor3DItems();
    }

    @Override
    public void displayLighting() {
        Lighting.setupForFlatItems();
    }

    /* =================================================================================================== State ==== */

    @Override
    public void color(float r, float g, float b, float a) {
        RenderSystem.color4f(r, g, b, a);
    }

    @Override
    public void enableDefaultBlend() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    @Override
    public void blendFunction(
            @MagicConstant(valuesFromClass = SourceFactor.class) int source,
            @MagicConstant(valuesFromClass = DestFactor.class) int dest
    ) {
        RenderSystem.blendFunc(source, dest);
    }

    /* ================================================================================================== Buffer ==== */

    @Override
    public void resetClearColor() {
        RenderSystem.clearColor(0, 0, 0, 0);
    }

    @Override
    public void clearColorBuffer() {
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.ON_OSX);
    }

    @Override
    public void clearDepthBuffer() {
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
    }

    /* ================================================================================================== Window ==== */

    @Override
    public void unbindFBO() {
        client.getMainRenderTarget().unbindWrite();
    }

    @Override
    public void flipFrame() {
        window.updateDisplay();
    }

    @Override
    public void rebindFBO() {
        client.getMainRenderTarget().bindWrite(false);
    }

    @Override
    public int getScaledWidth() {
        return window.getGuiScaledWidth();
    }

    @Override
    public int getScaledHeight() {
        return window.getGuiScaledHeight();
    }

    /* ============================================================================================= Projections ==== */

    @Override
    public void matrixModeProjection() {
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
    }

    @Override
    public void matrixModeModelView() {
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
    }

    @Override
    public void setupOverlayRendering() {
        Minecraft client = Minecraft.getInstance();
        Window window = client.getWindow();
        double scaleFactor = window.getGuiScale();

        RenderSystem.clear(GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
        RenderSystem.matrixMode(GL_PROJECTION);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0.0D, window.getWidth() / scaleFactor, window.getHeight() / scaleFactor, 0.0D, 1000.0D, 3000.0D);
        RenderSystem.matrixMode(GL_MODELVIEW);
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
    }
}
