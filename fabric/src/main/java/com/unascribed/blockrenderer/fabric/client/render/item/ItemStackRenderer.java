package com.unascribed.blockrenderer.fabric.client.render.item;


import com.mojang.blaze3d.platform.Window;
import com.unascribed.blockrenderer.fabric.client.varia.Identifiers;
import com.unascribed.blockrenderer.fabric.client.varia.rendering.GL;
import com.unascribed.blockrenderer.render.IAnimatedRenderer;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.Images;
import com.unascribed.blockrenderer.varia.Maths;
import com.unascribed.blockrenderer.varia.debug.Debug;
import com.unascribed.blockrenderer.varia.rendering.TileRenderer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.LongSupplier;

public class ItemStackRenderer implements IAnimatedRenderer<ItemStackParameters, ItemStack> {

    /**
     * {@link ItemRenderer#renderGuiItem(ItemStack, int, int)} -> renderGuiItem(BakedModel) uses 100F as Base Z Level
     */
    private static final int BASE_Z_LEVEL = 100;
    private static final float ITEM_STACK_SIZE = 16;

    private final ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

    private float blitOffset;

    @Nullable
    private TileRenderer tr;

    @Override
    public void setup(ItemStackParameters parameters) {
        Debug.push("item/setup");

        Window window = Minecraft.getInstance().getWindow();
        int displayWidth = window.getWidth();
        int displayHeight = window.getHeight();

        int size = Maths.minimum(displayHeight, displayWidth, parameters.size);
        tr = TileRenderer.forSize(parameters.size, size);

        /* Push Stack */
        GL.pushMatrix("item/setup");

        /* Setup Projection */
        GL.setupItemStackRendering(tr);

        /* Setup Lighting */
        GL.setupItemStackLighting();

        /* Scale based on desired size */
        // TODO: do we need to scale / translate in z axis
        float scale = parameters.size / ITEM_STACK_SIZE;
        GL.scaleFixedZLevel(scale, -BASE_Z_LEVEL);

        /* Save old blitOffset so we can reset it */
        blitOffset = renderer.blitOffset;

        /* Modify blitOffset */
        renderer.blitOffset = -BASE_Z_LEVEL / 2f;

        Debug.pop();
    }

    @Override
    public void render(ItemStack instance, ImageHandler<ItemStack> consumer, long nano) {
        assert tr != null;

        Debug.endFrame();
        Debug.push("item/" + Identifiers.get(instance.getItem()));

        /* Clear Pixel Buffer */
        tr.clearBuffer();

        /* Force Glint to be the same between renders by changing nano supplier */
        LongSupplier oldSupplier = Util.timeSource;
        Util.timeSource = () -> nano;

        Minecraft.getInstance().getTextureManager().tick();

        do {
            tr.beginTile();
            GL.pushMatrix("item/render");

            /* Clear Framebuffer */
            GL.clearFrameBuffer();

            /* Render */
            renderer.renderGuiItem(instance, 0, 0);

            GL.popMatrix("item/render");
        } while (tr.endTile());

        /* Reset nano supplier */
        Util.timeSource = oldSupplier;

        /* Pass the value and its resulting render to the consumer */
        /* Note: The rendered image needs to be flipped vertically */
        consumer.accept(instance, Images.fromTRFlipped(tr));

        Debug.pop();
    }

    @Override
    public void teardown() {
        Debug.push("item/teardown");

        /* Reset blitOffset */
        renderer.blitOffset = blitOffset;

        /* Pop Stack */
        GL.popMatrix("item/teardown");

        Debug.pop();
    }

}
