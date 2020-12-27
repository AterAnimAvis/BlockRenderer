package com.unascribed.blockrenderer.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.blockrenderer.client.api.Bounds;
import com.unascribed.blockrenderer.client.api.DefaultState;
import com.unascribed.blockrenderer.client.api.vendor.joml.*;
import com.unascribed.blockrenderer.client.render.IRenderer;
import com.unascribed.blockrenderer.client.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.client.varia.Identifiers;
import com.unascribed.blockrenderer.client.varia.Images;
import com.unascribed.blockrenderer.client.varia.Maths;
import com.unascribed.blockrenderer.client.varia.debug.Debug;
import com.unascribed.blockrenderer.client.varia.rendering.GL;
import com.unascribed.blockrenderer.client.varia.rendering.TileRenderer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Quaternion;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.lang.Math;
import java.util.function.LongSupplier;

import static com.unascribed.blockrenderer.client.varia.Maths.minimum;
import static java.lang.Math.round;

public class EntityRenderer implements IRenderer<EntityData, EntityData> {

    private static final Vector3dc unitCube = new Vector3d(1, 1, 1);

    private static final Minecraft client = Minecraft.getInstance();
    private static final MainWindow window = client.getMainWindow();
    private static final EntityRendererManager renderManager = client.getRenderManager();
    private static final IRenderTypeBuffer.Impl buffers = client.getRenderTypeBuffers().getBufferSource();

    private static final float PARTIAL_TICKS = 0f;

    @Override
    public void setup(EntityData data) {
        Debug.push("entity/setup");

        /* Push Stack */
        GL.pushMatrix();

        client.gameRenderer.getLightTexture().enableLightmap();

        /* Enable Normalize - This should fix Lighting */
        GL11.glEnable(GL11.GL_NORMALIZE);

        Debug.pop();
    }

    @Override
    public void render(EntityData data, ImageHandler<EntityData> consumer) {
        int tileWidth = window.getFramebufferWidth();
        int tileHeight = window.getFramebufferHeight();

        Debug.endFrame();
        Debug.push("entity/" + Identifiers.get(data.entity.getType()));

        /* Calculate Model View */
        final Matrix4d MV = new Matrix4d()
                .identity()
                .rotate(Math.toRadians(data.pitch), 1, 0, 0)
                .rotate(Math.toRadians(data.yaw), 0, 1, 0)
                .scale(data.scale, data.scale, data.scale);

        //TODO: Map Entity to RenderEntity
        Entity entity = data.entity;

        /* Get Render Bounds */
        Bounds bounds = Bounds.forEntity(entity);
        Vector3dc boundsSize = bounds.getSize();
        Vector3dc offset = bounds.getOffset();

        /* (Scaled by .5% to account for rounding errors) */
        final Matrix4d scaledMV = new Matrix4d(MV).scale(1.005, 1.005, 1.005);

        /* Calculate Projected Bounds */
        Vector4dc renderBounds = Maths.getProjectedBounds(scaledMV, boundsSize);
        int pW = (int) round(renderBounds.x());
        int pH = (int) round(renderBounds.y());
        double aspectRatio = renderBounds.x() / renderBounds.y();

        /* Setup Tile Renderer */
        @Nullable TileRenderer tr = TileRenderer.forSize(pW, pH, minimum(pW, tileWidth), minimum(pH, tileHeight));
        GL.setupEntityRendering(tr);

        /* Center */
        if (boundsSize.x() != boundsSize.z()) {
            /* Calculate Projected Bounds for a Unit Cube */
            Vector4dc unitBounds = Maths.getProjectedBounds(scaledMV, unitCube);
            int uW = (int) round(unitBounds.x());
            RenderSystem.translated(pW - uW / 2d, pH * aspectRatio / 2d, 0);
        } else {
            RenderSystem.translated(pW / 2d, pH * aspectRatio / 2d, 0);
        }

        /* Scale */
        RenderSystem.scalef(data.scale, data.scale, data.scale);

        /* Apply Transformation */
        RenderSystem.rotatef(data.pitch, 1f, 0f, 0f);
        RenderSystem.rotatef(data.yaw, 0f, 1f, 0f);

        /* Lighting */
        GL.setupWorldDiffuseLighting(new Matrix4f(MV));

        /* Force Glint to be the same between renders by changing nano supplier */
        LongSupplier oldSupplier = Util.nanoTimeSupplier;
        Util.nanoTimeSupplier = () -> 0L;

        /* Disable Debug Bounding Box */
        boolean isDebugBoundingBox = renderManager.isDebugBoundingBox();
        renderManager.setDebugBoundingBox(false);

        /* Disable Entity Shadows */
        renderManager.setRenderShadow(false);

        /* Set Orientation */
        renderManager.setCameraOrientation(Quaternion.ONE);

        /* Set Entity Age */
        int ticksExisted = entity.ticksExisted;
        if (data.fixedAge >= 0) entity.ticksExisted = data.fixedAge;
        else entity.ticksExisted = DefaultState.forEntity(entity).fixedAge();

        do {
            tr.beginTile();
            RenderSystem.pushMatrix();

            /* Clear Framebuffer */
            RenderSystem.clearColor(0, 0, 0, 0);
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);

            MatrixStack stack = new MatrixStack();

            /* Render */
            renderManager.renderEntityStatic(entity, offset.x(), offset.y(), offset.z(), 0F, PARTIAL_TICKS, stack, buffers, renderManager.getPackedLight(entity, PARTIAL_TICKS));

            /* Draw Unit Cube Bounds with Offset */
            WorldRenderer.drawBoundingBox(stack, buffers.getBuffer(RenderType.getLines()), offset.x(), offset.y(), offset.z(), 1 + offset.x(), 1 + offset.y(), 1 + offset.z(), 0.0F, 0.0F, 1.0F, 1.0F);

            /* Draw Render Bounds */
            WorldRenderer.drawBoundingBox(stack, buffers.getBuffer(RenderType.getLines()), 0, 0, 0, boundsSize.x(), boundsSize.y(), boundsSize.z(), 1.0F, 0.0F, 0.0F, 1.0F);

            buffers.finish();

            RenderSystem.popMatrix();
        } while (tr.endTile());

        consumer.accept(data, Images.fromTR(tr));

        /* Reset Entity Age */
        entity.ticksExisted = ticksExisted;

        /* Reset Entity Shadows */
        renderManager.setRenderShadow(true);

        /* Reset Debug Bounding Box */
        renderManager.setDebugBoundingBox(isDebugBoundingBox);

        /* Reset nano supplier */
        Util.nanoTimeSupplier = oldSupplier;
        Debug.pop();
    }

    @Override
    public void teardown() {
        Debug.push("entity/teardown");

        client.gameRenderer.getLightTexture().disableLightmap();

        /* Disable Normalize */
        GL11.glDisable(GL11.GL_NORMALIZE);

        /* Pop Stack */
        GL.popMatrix();

        Debug.pop();
    }

}
