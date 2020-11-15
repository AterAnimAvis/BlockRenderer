package com.unascribed.blockrenderer.fabric.client.render.map;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.render.map.MapDecorations;
import com.unascribed.blockrenderer.render.map.MapParameters;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.Images;
import com.unascribed.blockrenderer.varia.Maths;
import com.unascribed.blockrenderer.varia.debug.Debug;
import com.unascribed.blockrenderer.varia.rendering.TileRenderer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

import java.util.function.LongSupplier;

import static com.unascribed.blockrenderer.Interop.GL;

public class MapRenderer implements IRenderer<MapParameters, MapItemSavedData> {

    private static final float MAP_SIZE = 128.0F;

    @Nullable
    private TileRenderer tr;
    private MapDecorations decorations = MapDecorations.ALL;

    @Override
    public void setup(MapParameters parameters) {
        Debug.push("map/setup");

        decorations = parameters.decorations;

        Window window = Minecraft.getInstance().getWindow();
        int displayWidth = window.getWidth();
        int displayHeight = window.getHeight();

        int size = Maths.minimum(displayHeight, displayWidth, parameters.size);
        tr = TileRenderer.forSize(parameters.size, size);

        /* Push Stack */
        GL.pushMatrix("map/setup");

        /* Setup Projection */
        GL.setupMapRendering(tr);

        /* Setup Lighting */
        GL.displayLighting();

        /* Scale based on desired size */
        float scale = parameters.size / MAP_SIZE;
        GL.scale(scale, scale, decorations == MapDecorations.NONE ? 1 : -1);

        Debug.pop();
    }

    @Override
    public void render(MapItemSavedData instance, ImageHandler<MapItemSavedData> consumer) {
        assert tr != null;

        Debug.endFrame();
        Debug.push("map/render");

        /* Clear Pixel Buffer */
        tr.clearBuffer();

        /* Force Glint to be the same between renders by changing nano supplier */
        LongSupplier oldSupplier = Util.timeSource;
        Util.timeSource = () -> 0;

        Minecraft.getInstance().getTextureManager().tick();

        MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        try (net.minecraft.client.gui.MapRenderer renderer = new net.minecraft.client.gui.MapRenderer(Minecraft.getInstance().getTextureManager())) {
            renderer.update(instance);

            do {
                tr.beginTile();
                GL.pushMatrix("map/render");

                /* Clear Framebuffer */
                GL.clearFrameBuffer();

                /* Render (MatrixStack, Buffers, Data, RenderBorder, LightMap) */
                renderer.render(new PoseStack(), buffers, instance, decorations != MapDecorations.ALL, 240);

                buffers.endBatch();

                GL.popMatrix("map/render");
            } while (tr.endTile());
        }

        /* Reset nano supplier */
        Util.timeSource = oldSupplier;

        /* Pass the value and its resulting render to the consumer */
        /* Note: The rendered image needs to be flipped vertically */
        consumer.accept(instance, Images.fromTRFlipped(tr));

        Debug.pop();
    }

    @Override
    public void teardown() {
        Debug.push("map/teardown");

        /* Pop Stack */
        GL.popMatrix("map/teardown");

        Debug.pop();
    }

}
