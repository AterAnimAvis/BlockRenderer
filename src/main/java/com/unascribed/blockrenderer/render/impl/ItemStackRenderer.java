package com.unascribed.blockrenderer.render.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.blockrenderer.lib.TileRenderer;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.utils.Rendering;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
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
    private static final MainWindow window = client.mainWindow;
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
        GlStateManager.pushMatrix();

        /* Setup Projection */
        Rendering.setupOverlayRendering(renderer);

        /* Setup Lighting */
        RenderHelper.enableGUIStandardItemLighting();

        /* Scale based on desired size */
        float scale = desiredSize / 16f;
        GlStateManager.translatef(0, 0, -scale * 100);
        GlStateManager.scalef(scale, scale, scale);

        /* Flip culling due to the flipped projection */
        GL11.glCullFace(GL11.GL_FRONT);

        /* 1.14.4 - DepthTest needs to be enabled for Enchanted Books */
        GlStateManager.enableDepthTest();

        /* 1.14.4 - Blend needs to be enabled for Glass */
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

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

            GlStateManager.pushMatrix();

            /* Clear Framebuffer */
            GlStateManager.clearColor(0, 0, 0, 0);
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);

            /* Render */
            itemRenderer.renderItemAndEffectIntoGUI(value, 0, 0);

            GlStateManager.popMatrix();
        } while (renderer.endTile());

        /* Reset nano supplier */
        Util.nanoTimeSupplier = oldSupplier;
    }

    @Override
    public void teardown() {
        /* Reset zLevel */
        itemRenderer.zLevel = oldZLevel;

        /* Reset Blend */
        GlStateManager.disableBlend();

        /* Reset Depth Test */
        GlStateManager.disableDepthTest();

        /* Reset Culling */
        GL11.glCullFace(GL11.GL_BACK);

        /* Pop Stack */
        GlStateManager.popMatrix();
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
    public void renderTooltip(ItemStack value, int displayWidth, int displayHeight) {
        List<String> list = getTooltipFromItem(value);

        // This code is copied from the tooltip renderer, so we can properly center it.
        FontRenderer font = value.getItem().getFontRenderer(value);
        if (font == null) font = client.fontRenderer;

        int width = 0;
        for (String s : list) {
            int j = font.getStringWidth(s);
            if (j > width) width = j;
        }
        // End copied code.

        GlStateManager.translatef((displayWidth - width / 2f) - 12, displayHeight + 30, 0);
        Rendering.drawHoveringText(list, 0, 0, font);
    }

}
