package com.unascribed.blockrenderer.forge.client.varia.rendering;

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
        RenderHelper.setupGui3DDiffuseLighting();
    }

    @Override
    public void displayLighting() {
        RenderHelper.disableStandardItemLighting();
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
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
    }

    @Override
    public void clearDepthBuffer() {
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
    }

    /* ================================================================================================== Window ==== */

    @Override
    public void unbindFBO() {
        client().getFramebuffer().unbindFramebuffer();
    }

    @Override
    public void flipFrame() {
        window().flipFrame();
    }

    @Override
    public void rebindFBO() {
        client().getFramebuffer().bindFramebuffer(false);
    }

    @Override
    public int getScaledWidth() {
        return window().getScaledWidth();
    }

    @Override
    public int getScaledHeight() {
        return window().getScaledHeight();
    }

    @Override
    public int getFramebufferWidth() {
        return window().getFramebufferWidth();
    }

    @Override
    public int getFramebufferHeight() {
        return window().getFramebufferHeight();
    }

    @Override
    public double getScaleFactor() {
        return window().getGuiScaleFactor();
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
        return Minecraft.getInstance().getMainWindow();
    }

}
