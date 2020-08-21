package com.unascribed.blockrenderer.render.impl;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.blockrenderer.lib.TileRenderer;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.utils.Rendering;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.LongSupplier;

import static com.unascribed.blockrenderer.utils.MathUtils.minimum;
import static com.unascribed.blockrenderer.utils.StringUtils.getTooltipFromItem;

public class ItemStackRenderer implements IRenderer<ItemStack> {

    private static final Minecraft client = Minecraft.getInstance();
    private static final MainWindow window = client.getMainWindow();
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
        RenderHelper.setupGui3DDiffuseLighting();

        /* Scale based on desired size */
        float scale = desiredSize / 16f;
        RenderSystem.translatef(0, 0, -scale * 100);
        RenderSystem.scalef(scale, scale, scale);

        /* Flip culling due to the flipped projection */
        GL11.glCullFace(GL11.GL_FRONT);

        /* Modify zLevel */
        oldZLevel = itemRenderer.zLevel;
        itemRenderer.zLevel = -50;
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
                RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);

                /* Render */
                itemRenderer.renderItemAndEffectIntoGUI(value, 0, 0);

            RenderSystem.popMatrix();
        } while (renderer.endTile());

        /* Reset nano supplier */
        Util.nanoTimeSupplier = oldSupplier;
    }

    @Override
    public void teardown() {
        /* Reset zLevel */
        itemRenderer.zLevel = oldZLevel;

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
    public ResourceLocation getId(ItemStack value) {
        ResourceLocation identifier = ForgeRegistries.ITEMS.getKey(value.getItem());

        if (identifier == null) return new ResourceLocation("air");

        return identifier;
    }

    @Override
    public ITextComponent getName(ItemStack value) {
        return value.getDisplayName();
    }

    @Override
    public void renderTooltip(MatrixStack stack, ItemStack value, int displayWidth, int displayHeight) {
        List<ITextComponent> list = getTooltipFromItem(value);
        // This code is copied from the tooltip renderer, so we can properly center it.
        FontRenderer font = value.getItem().getFontRenderer(value);
        if (font == null) font = client.fontRenderer;

        int width = 0;
        for (IReorderingProcessor s : Lists.transform(list, ITextComponent::func_241878_f)) {
            int j = font.func_243245_a(s);
            if (j > width) width = j;
        }
        // End copied code.

        RenderSystem.translatef((displayWidth - width / 2f) - 12, displayHeight + 30, 0);
        Rendering.drawHoveringText(stack, list, 0, 0);
    }

}
