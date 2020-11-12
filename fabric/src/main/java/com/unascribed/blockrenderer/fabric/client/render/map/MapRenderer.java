package com.unascribed.blockrenderer.fabric.client.render.map;

import com.unascribed.blockrenderer.fabric.client.varia.rendering.GL;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.Images;
import com.unascribed.blockrenderer.varia.Maths;
import com.unascribed.blockrenderer.varia.debug.Debug;
import com.unascribed.blockrenderer.varia.rendering.TileRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.function.LongSupplier;

public class MapRenderer implements IRenderer<MapParameters, MapState> {

    private static final float MAP_SIZE = 128.0F;

    @Nullable
    private TileRenderer tr;
    private MapDecorations decorations = MapDecorations.ALL;

    @Override
    public void setup(MapParameters parameters) {
        Debug.push("map/setup");

        decorations = parameters.decorations;

        Window window = MinecraftClient.getInstance().getWindow();
        int displayWidth = window.getFramebufferWidth();
        int displayHeight = window.getFramebufferHeight();

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
    public void render(MapState instance, ImageHandler<MapState> consumer) {
        assert tr != null;

        Debug.endFrame();
        Debug.push("map/render");

        /* Clear Pixel Buffer */
        tr.clearBuffer();

        /* Force Glint to be the same between renders by changing nano supplier */
        LongSupplier oldSupplier = Util.nanoTimeSupplier;
        Util.nanoTimeSupplier = () -> 0;

        MinecraftClient.getInstance().getTextureManager().tick();

        VertexConsumerProvider.Immediate buffers = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        try (net.minecraft.client.gui.MapRenderer renderer = new net.minecraft.client.gui.MapRenderer(MinecraftClient.getInstance().getTextureManager())) {
            renderer.updateTexture(instance);

            do {
                tr.beginTile();
                GL.pushMatrix("map/render");

                /* Clear Framebuffer */
                GL.clearFrameBuffer();

                /* Render (MatrixStack, Buffers, Data, RenderBorder, LightMap) */
                renderer.draw(new MatrixStack(), buffers, instance, decorations != MapDecorations.ALL, 240);

                buffers.draw();

                GL.popMatrix("map/render");
            } while (tr.endTile());
        }

        /* Reset nano supplier */
        Util.nanoTimeSupplier = oldSupplier;

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
