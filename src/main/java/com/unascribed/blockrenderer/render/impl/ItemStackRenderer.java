package com.unascribed.blockrenderer.render.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.blockrenderer.lib.TileRenderer;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.utils.Rendering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.LongSupplier;

import static com.unascribed.blockrenderer.utils.MathUtils.minimum;
import static com.unascribed.blockrenderer.utils.StringUtils.getTooltipFromItem;

public class ItemStackRenderer implements IRenderer<ItemStack> {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Window window = client.getWindow();
    private static final ItemRenderer itemRenderer = client.getItemRenderer();

    private float oldZLevel;

    private TileRenderer renderer;

    @Override
    public void setup(int desiredSize) {
        int displayWidth = window.getFramebufferWidth();
        int displayHeight = window.getFramebufferHeight();

        int size = minimum(displayHeight, displayWidth, desiredSize);
        renderer = TileRenderer.forSize(desiredSize, size);

        /* Push Stack */
        RenderSystem.pushMatrix();

        /* Setup Projection */
        Rendering.setupOverlayRendering(renderer);

        /* Setup Lighting */
        DiffuseLighting.enableGuiDepthLighting();

        /* Scale based on desired size */
        float scale = desiredSize / 16f;
        RenderSystem.translatef(0, 0, -scale * 100);
        RenderSystem.scalef(scale, scale, scale);

        /* Flip culling due to the flipped projection */
        GL11.glCullFace(GL11.GL_FRONT);

        oldZLevel = itemRenderer.zOffset;
        itemRenderer.zOffset = -50;
    }

    @Override
    public void render(ItemStack value) {
        /* Clear Pixel Buffer */
        renderer.buffer.clear();

        /* Force Glint to be the same between renders by changing nano supplier */
        LongSupplier oldSupplier = Util.nanoTimeSupplier;
        Util.nanoTimeSupplier = () -> 0L;

        do {
            renderer.beginTile();

            RenderSystem.pushMatrix();

            /* Clear Framebuffer */
            RenderSystem.clearColor(0, 0, 0, 0);
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);

            /* Render */
            itemRenderer.renderInGuiWithOverrides(value, 0, 0);

            RenderSystem.popMatrix();
        } while (renderer.endTile());

        /* Reset nano supplier */
        Util.nanoTimeSupplier = oldSupplier;
    }

    @Override
    public void teardown() {
        /* Reset zLevel */
        itemRenderer.zOffset = oldZLevel;

        /* Reset Culling */
        GL11.glCullFace(GL11.GL_BACK);

        /* Pop Stack */
        RenderSystem.popMatrix();
    }

    @Override
    @Nullable
    public TileRenderer getRenderer() {
        return renderer;
    }

    @Override
    public Identifier getId(ItemStack value) {
        return Registry.ITEM.getId(value.getItem());
    }

    @Override
    public Text getName(ItemStack value) {
        return value.getName();
    }

    @Override
    public void renderTooltip(MatrixStack stack, ItemStack value, int displayWidth, int displayHeight) {
        List<Text> list = getTooltipFromItem(value);

        // This code is copied from the tooltip renderer, so we can properly center it.
        TextRenderer font = client.textRenderer;

        int width = 0;
        for (Text s : list) {
            int j = font.getWidth(s);
            if (j > width) width = j;
        }
        // End copied code.

        RenderSystem.translatef((displayWidth - width / 2f) - 12, displayHeight + 30, 0);
        Rendering.drawHoveringText(stack, list, 0, 0);
    }

}
