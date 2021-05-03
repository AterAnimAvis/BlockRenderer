package com.unascribed.blockrenderer.fabric.client.render.map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.InternalAPI;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.render.map.MapDecorations;
import com.unascribed.blockrenderer.render.map.MapParameters;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.Images;
import com.unascribed.blockrenderer.varia.Maths;
import com.unascribed.blockrenderer.varia.debug.Debug;
import com.unascribed.blockrenderer.varia.rendering.GLI;
import com.unascribed.blockrenderer.varia.rendering.TileRenderer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Util;
import net.minecraft.world.storage.MapData;
import org.jetbrains.annotations.Nullable;

import java.util.function.LongSupplier;

public class MapRenderer implements IRenderer<MapParameters, MapData> {

    private static final float MAP_SIZE = 128.0F;

    private final GLI GL = InternalAPI.getGL();

    @Nullable
    private TileRenderer tr;
    private MapDecorations decorations = MapDecorations.ALL;

    @Override
    public void setup(MapParameters parameters) {
        Debug.push("map/setup");

        decorations = parameters.decorations;

        MainWindow window = Minecraft.getInstance().getWindow();
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
    public void render(MapData instance, ImageHandler<MapData> consumer) {
        assert tr != null;

        Debug.endFrame();
        Debug.push("map/render");

        /* Clear Pixel Buffer */
        tr.clearBuffer();

        /* Force Glint to be the same between renders by changing timeSource */
        LongSupplier oldSupplier = Util.timeSource;
        Util.timeSource = () -> 0;

        Minecraft.getInstance().getTextureManager().tick();

        IRenderTypeBuffer.Impl buffers = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        try (MapItemRenderer renderer = new MapItemRenderer(Minecraft.getInstance().getTextureManager())) {
            renderer.update(instance);

            do {
                tr.beginTile();
                GL.pushMatrix("map/render");

                /* Clear Framebuffer */
                GL.clearFrameBuffer();

                /* Render (MatrixStack, Buffers, Data, RenderBorder, LightMap) */
                renderer.render(new MatrixStack(), buffers, instance, decorations != MapDecorations.ALL, 240);

                buffers.endBatch();

                GL.popMatrix("map/render");
            } while (tr.endTile());
        }

        /* Reset timeSource */
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
