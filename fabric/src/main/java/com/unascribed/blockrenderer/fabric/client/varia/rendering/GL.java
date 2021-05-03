package com.unascribed.blockrenderer.fabric.client.varia.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.blockrenderer.varia.rendering.GLI;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import org.intellij.lang.annotations.MagicConstant;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("deprecation")
public class GL implements GLI {

    public static final GLI INSTANCE = new GL();

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

    @Override
    public void ortho(double left, double right, double bottom, double top, double zNear, double zFar) {
        RenderSystem.ortho(left, right, bottom, top, zNear, zFar);
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
        RenderHelper.setupFor3DItems();
    }

    @Override
    public void displayLighting() {
        RenderHelper.setupForFlatItems();
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
        RenderSystem.clearColor(1F, 1F, 1F, 0F);
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
        client().getMainRenderTarget().unbindWrite();
    }

    @Override
    public void flipFrame() {
        window().updateDisplay();
    }

    @Override
    public void rebindFBO() {
        client().getMainRenderTarget().bindWrite(false);
    }

    @Override
    public int getScaledWidth() {
        return window().getGuiScaledWidth();
    }

    @Override
    public int getScaledHeight() {
        return window().getGuiScaledHeight();
    }

    @Override
    public int getFramebufferWidth() {
        return window().getWidth();
    }

    @Override
    public int getFramebufferHeight() {
        return window().getHeight();
    }

    @Override
    public double getScaleFactor() {
        return window().getGuiScale();
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

    /* ================================================================================================= Utility ==== */

    private Minecraft client() {
        return Minecraft.getInstance();
    }

    private MainWindow window() {
        return Minecraft.getInstance().getWindow();
    }

}
