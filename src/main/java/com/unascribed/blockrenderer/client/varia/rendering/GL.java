package com.unascribed.blockrenderer.client.varia.rendering;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.blockrenderer.Reference;
import com.unascribed.blockrenderer.client.api.vendor.joml.Matrix4f;
import com.unascribed.blockrenderer.client.api.vendor.joml.Vector3f;
import com.unascribed.blockrenderer.client.api.vendor.joml.Vector4f;
import com.unascribed.blockrenderer.client.varia.logging.Log;
import com.unascribed.blockrenderer.client.varia.logging.Markers;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

@SuppressWarnings("deprecation")
public interface GL {

    Minecraft client = Minecraft.getInstance();
    MainWindow window = client.getMainWindow();

    /* ================================================================================================== Matrix ==== */

    static void pushMatrix() {
        RenderSystem.pushMatrix();
    }

    static void pushMatrix(String debug) {
        RenderSystem.pushMatrix();
        if (Reference.isDebug)
            Log.debug(Markers.OPEN_GL_DEBUG, "> Push Matrix {}", debug);
    }

    static void popMatrix() {
        RenderSystem.popMatrix();
    }

    static void popMatrix(String debug) {
        RenderSystem.popMatrix();
        if (Reference.isDebug)
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
        RenderHelper.setupGui3DDiffuseLighting();
    }

    static void displayLighting() {
        RenderHelper.disableStandardItemLighting();
    }

    Vector4f BUFFER = new Vector4f();
    Vector3f DIFFUSE_LIGHT_0 = new Vector3f(0.2F, 1.0F, -0.7F).normalize();
    Vector3f DIFFUSE_LIGHT_1 = new Vector3f(-0.2F, 1.0F, 0.7F).normalize();
    FloatBuffer FLOAT_4_BUFFER = GLAllocation.createDirectFloatBuffer(4);

    static void setupWorldDiffuseLighting(Matrix4f transform) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        RenderSystem.pushMatrix();

        RenderSystem.loadIdentity();

        /* Enable Light Sources */
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);

        /* Setup Light 0 */
        BUFFER.set(DIFFUSE_LIGHT_0, 1.0f).mul(transform);
        setupLighting(GL_LIGHT0, BUFFER);

        /* Setup Light 1 */
        BUFFER.set(DIFFUSE_LIGHT_1, 1.0f).mul(transform);
        setupLighting(GL_LIGHT1, BUFFER);

        RenderSystem.shadeModel(GL_FLAT);

        /* Setup Ambient Light */
        GlStateManager.lightModel(GL_LIGHT_MODEL_AMBIENT, getBuffer(0.4F, 0.4F, 0.4F, 1.0F));

        RenderSystem.popMatrix();
    }

    static void setupLighting(int light, Vector4f v) {
        GlStateManager.light(light, GL_POSITION, getBuffer(v.x, v.y, v.z, 0.0F));
        GlStateManager.light(light, GL_DIFFUSE, getBuffer(0.6F, 0.6F, 0.6F, 1.0F));
        GlStateManager.light(light, GL_AMBIENT, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.light(light, GL_SPECULAR, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
    }

    static FloatBuffer getBuffer(float f0, float f1, float f2, float f3) {
        FLOAT_4_BUFFER.clear();
        FLOAT_4_BUFFER.put(f0).put(f1).put(f2).put(f3);
        FLOAT_4_BUFFER.flip();
        return FLOAT_4_BUFFER;
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
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
    }

    static void clearDepthBuffer() {
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
    }

    /* ================================================================================================== Window ==== */

    static void unbindFBO() {
        client.getFramebuffer().unbindFramebuffer();
    }

    static void flipFrame() {
        window.flipFrame();
    }

    static void rebindFBO() {
        client.getFramebuffer().bindFramebuffer(false);
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

    static void setupEntityRendering(TileRenderer renderer) {
        RenderSystem.clear(GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);

        /* Projection */
        RenderSystem.matrixMode(GL_PROJECTION);
        RenderSystem.loadIdentity();

        renderer.orthographic(0.0D, renderer.getImageWidth(), renderer.getImageHeight(), 0, -9000.0D, 9000.0D);

        /* Model View */
        RenderSystem.matrixMode(GL_MODELVIEW);
        RenderSystem.loadIdentity();
    }

}
