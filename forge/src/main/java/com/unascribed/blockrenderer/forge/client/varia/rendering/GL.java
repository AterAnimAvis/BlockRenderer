package com.unascribed.blockrenderer.forge.client.varia.rendering;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.blockrenderer.Reference;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import com.unascribed.blockrenderer.varia.rendering.TileRenderer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

@SuppressWarnings("deprecation")
public interface GL {

    Minecraft client = Minecraft.getInstance();
    MainWindow window = client.getWindow();

    /* ================================================================================================== Matrix ==== */

    static void pushMatrix() {
        RenderSystem.pushMatrix();
    }

    static void pushMatrix(String debug) {
        RenderSystem.pushMatrix();
        if (Reference.DEVELOPMENT)
            Log.debug(Markers.OPEN_GL_DEBUG, "> Push Matrix {}", debug);
    }

    static void popMatrix() {
        RenderSystem.popMatrix();
    }

    static void popMatrix(String debug) {
        RenderSystem.popMatrix();
        if (Reference.DEVELOPMENT)
            Log.debug(Markers.OPEN_GL_DEBUG, "< Pop Matrix {}", debug);
    }

    static void loadIdentity() {
        RenderSystem.loadIdentity();
    }

    /* ========================================================================================= Transformations ==== */

    static void translate(float x, float y, float z) {
        RenderSystem.translatef(x, y, z);
    }

    static void scale(float x, float y, float z) {
        RenderSystem.scalef(x, y, z);
    }

    static void scaleFixedZLevel(float scale, float zLevel) {
        translate(0F, 0F, scale * zLevel);
        scale(scale, scale, scale);
    }

    /* ================================================================================================ Lighting ==== */

    static void setupItemStackLighting() {
        RenderHelper.setupFor3DItems();
    }

    static void displayLighting() {
        RenderHelper.setupForFlatItems();
    }

    /* =================================================================================================== State ==== */

    static void color(float r, float g, float b, float a) {
        RenderSystem.color4f(r, g, b, a);
    }

    static void enableDefaultBlend() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    static void blendFunction(GlStateManager.SourceFactor source, GlStateManager.DestFactor dest) {
        RenderSystem.blendFunc(source, dest);
    }

    /* ================================================================================================== Buffer ==== */

    static void clearFrameBuffer() {
        resetClearColor();
        clearColorBuffer();
        clearDepthBuffer();
    }

    static void resetClearColor() {
        RenderSystem.clearColor(0, 0, 0, 0);
    }

    static void clearColorBuffer() {
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.ON_OSX);
    }

    static void clearDepthBuffer() {
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
    }

    /* ================================================================================================== Window ==== */

    static void unbindFBO() {
        client.getMainRenderTarget().unbindWrite();
    }

    static void flipFrame() {
        window.updateDisplay();
    }

    static void rebindFBO() {
        client.getMainRenderTarget().bindWrite(false);
    }

    /* ============================================================================================= Projections ==== */

    static void matrixModeProjection() {
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
    }

    static void matrixModeModelView() {
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
    }

    static void setupItemStackRendering(TileRenderer tr) {
        clearDepthBuffer();

        /* Projection */
        matrixModeProjection();
        loadIdentity();

        /* Setup Orthographic Projection */
        /*
            Whilst we can switch the top/bottom parameters to save flipping the image later,
            culling issues sometime occur.
        */
        tr.orthographic(0.0, tr.getImageWidth(), tr.getImageHeight(), 0, 1000.0, 3000.0);

        /* Model View */
        matrixModeModelView();
        loadIdentity();
        translate(0f, 0f, -2000f);
    }

    static void setupMapRendering(TileRenderer tr) {
        clearDepthBuffer();

        /* Projection */
        matrixModeProjection();
        loadIdentity();

        //TODO: Reduce Near / Far
        tr.orthographic(0.0, tr.getImageWidth(), tr.getImageHeight(), 0, -1000.0F, 3000.0f);

        /* Model View */
        matrixModeModelView();
        loadIdentity();
    }

    static void setupOverlayRendering() {
        Minecraft client = Minecraft.getInstance();
        MainWindow window = client.getWindow();
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
