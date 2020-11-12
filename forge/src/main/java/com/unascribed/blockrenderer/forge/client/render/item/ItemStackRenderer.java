package com.unascribed.blockrenderer.forge.client.render.item;

import com.unascribed.blockrenderer.forge.client.varia.Identifiers;
import com.unascribed.blockrenderer.forge.client.varia.rendering.GL;
import com.unascribed.blockrenderer.render.IAnimatedRenderer;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.Images;
import com.unascribed.blockrenderer.varia.Maths;
import com.unascribed.blockrenderer.varia.debug.Debug;
import com.unascribed.blockrenderer.varia.rendering.TileRenderer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.function.LongSupplier;

public class ItemStackRenderer implements IAnimatedRenderer<ItemStackParameters, ItemStack> {

    /**
     * {@link ItemRenderer#renderItemIntoGUI(ItemStack, int, int)} -> renderItemModelIntoGUI uses 100F as Base Z Level
     */
    private static final int BASE_Z_LEVEL = 100;
    private static final float ITEM_STACK_SIZE = 16;

    private final ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

    private float zLevel;

    @Nullable
    private TileRenderer tr;

    @Override
    public void setup(ItemStackParameters parameters) {
        Debug.push("item/setup");

        MainWindow window = Minecraft.getInstance().getMainWindow();
        int displayWidth = window.getFramebufferWidth();
        int displayHeight = window.getFramebufferHeight();

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

        /* Save old zLevel so we can reset it */
        zLevel = renderer.zLevel;

        /* Modify zLevel */
        renderer.zLevel = -BASE_Z_LEVEL / 2f;

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
        LongSupplier oldSupplier = Util.nanoTimeSupplier;
        Util.nanoTimeSupplier = () -> nano;

        Minecraft.getInstance().textureManager.tick();

        do {
            tr.beginTile();
            GL.pushMatrix("item/render");

            /* Clear Framebuffer */
            GL.clearFrameBuffer();

            /* Render */
            renderer.renderItemAndEffectIntoGUI(instance, 0, 0);

            GL.popMatrix("item/render");
        } while (tr.endTile());

        /* Reset nano supplier */
        Util.nanoTimeSupplier = oldSupplier;

        /* Pass the value and its resulting render to the consumer */
        /* Note: The rendered image needs to be flipped vertically */
        consumer.accept(instance, Images.fromTRFlipped(tr));

        Debug.pop();
    }

    @Override
    public void teardown() {
        Debug.push("item/teardown");

        /* Reset zLevel */
        renderer.zLevel = zLevel;

        /* Pop Stack */
        GL.popMatrix("item/teardown");

        Debug.pop();
    }

}
