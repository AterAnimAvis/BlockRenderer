package com.unascribed.blockrenderer.fabric.client.varia.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.blockrenderer.varia.rendering.GLI;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import org.intellij.lang.annotations.MagicConstant;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

@SuppressWarnings("deprecation")
public class GL implements GLI {

    public static final GLI INSTANCE = new GL();

    Minecraft client = Minecraft.getInstance();
    MainWindow window = client.getMainWindow();

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
        client.getFramebuffer().unbindFramebuffer();
    }

    @Override
    public void flipFrame() {
        window.flipFrame();
    }

    @Override
    public void rebindFBO() {
        client.getFramebuffer().bindFramebuffer(false);
    }

    @Override
    public int getScaledWidth() {
        return window.getScaledWidth();
    }

    @Override
    public int getScaledHeight() {
        return window.getScaledHeight();
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
        MainWindow window = client.getMainWindow();
        double scaleFactor = window.getGuiScaleFactor();

        RenderSystem.clear(GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
        RenderSystem.matrixMode(GL_PROJECTION);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0.0D, window.getFramebufferWidth() / scaleFactor,
                window.getFramebufferHeight() / scaleFactor, 0.0D, 1000.0D, 3000.0D);
        RenderSystem.matrixMode(GL_MODELVIEW);
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
    }
}
