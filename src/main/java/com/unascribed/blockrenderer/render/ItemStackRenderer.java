package com.unascribed.blockrenderer.render;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.blockrenderer.lib.TileRenderer;
import com.unascribed.blockrenderer.utils.ImageUtils;
import com.unascribed.blockrenderer.utils.Rendering;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.function.LongSupplier;

import static com.unascribed.blockrenderer.utils.FileUtils.getFile;
import static com.unascribed.blockrenderer.utils.MathUtils.minimum;
import static com.unascribed.blockrenderer.utils.MiscUtils.collectStacks;
import static com.unascribed.blockrenderer.utils.MiscUtils.isEscapePressed;
import static com.unascribed.blockrenderer.utils.StringUtils.*;

public class ItemStackRenderer {

    public static final File DEFAULT_FOLDER = new File("renders");

    private static final Minecraft client = Minecraft.getInstance();
    private static final MainWindow window = client.mainWindow;
    private static final ItemRenderer itemRenderer = client.getItemRenderer();

    private float oldZLevel;

    private TileRenderer renderer;

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

    public void render(ItemStack stack) {
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
                itemRenderer.renderItemAndEffectIntoGUI(stack, 0, 0);

            GlStateManager.popMatrix();
        } while (renderer.endTile());

        /* Reset nano supplier */
        Util.nanoTimeSupplier = oldSupplier;

    }

    @SuppressWarnings("UnstableApiUsage")
    public ITextComponent save(File folder, String filename) {
        try {
            BufferedImage img = ImageUtils.readPixels(renderer);

            File file = getFile(folder, filename);
            Files.createParentDirs(file);

            ImageIO.write(img, "PNG", file);
            return getRenderSuccess(folder, file);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new TranslationTextComponent("msg.blockrenderer.render.fail");
        }
    }

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

    public static void renderItem(int size, ItemStack stack, boolean useId, boolean addSize) {
        ItemStackRenderer renderer = new ItemStackRenderer();

        ResourceLocation identifier = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (identifier == null) identifier = new ResourceLocation("air");

        String sizeString = addSize ? size + "x" + size + "_" : "";
        String fileName = useId ? sanitize(identifier.toString()) : sanitize(stack.getDisplayName());

        renderer.setup(size);
        renderer.render(stack);
        addMessage(renderer.save(DEFAULT_FOLDER, dateTime() + "_" + sizeString + fileName));
        renderer.teardown();
    }

    public static void bulkRender(int size, String namespaceSpec, boolean useId, boolean addSize) {
        Set<String> namespaces = getNamespaces(namespaceSpec);
        List<ItemStack> renders = collectStacks(namespaces);
        String joined = Joiner.on(", ").join(namespaces);

        if (renders.size() < 1) {
            addMessage(new TranslationTextComponent("msg.blockrenderer.bulk.noItems", joined));
            return;
        }

        client.displayGuiScreen(new IngameMenuScreen(false));

        int rendered = 0;
        long lastUpdate = 0;
        int total = renders.size();

        String sizeString = addSize ? size + "x" + size + "_" : "";
        File folder = new File(DEFAULT_FOLDER, dateTime() + "_" + sizeString + sanitize(namespaceSpec) + "/");
        String title = I18n.format("blockrenderer.gui.rendering", total, joined);

        long start = Util.milliTime();

        ItemStackRenderer renderer = new ItemStackRenderer();

        renderer.setup(size);
        for (ItemStack stack : renders) {
            if (isEscapePressed()) break;

            ResourceLocation identifier = ForgeRegistries.ITEMS.getKey(stack.getItem());
            if (identifier == null) identifier = new ResourceLocation("air");

            String fileName = useId ? sanitize(identifier.toString()) : sanitize(stack.getDisplayName());

            renderer.render(stack);
            renderer.save(folder, fileName);
            rendered++;

            if (Util.milliTime() - lastUpdate > 33) {
                renderer.teardown();

                renderLoading(title, I18n.format("blockrenderer.gui.progress", rendered, total, (total - rendered)), stack, (float)rendered/ total);

                lastUpdate = Util.milliTime();
                renderer.setup(size);
            }
        }

        long elapsed = Util.milliTime() - start;

        if (rendered >= total) {
            renderLoading(I18n.format("blockrenderer.gui.rendered", total, joined), "", 1f);
            addMessage(new TranslationTextComponent("msg.blockrenderer.bulk.finished", total, joined, asClickable(folder)));
        } else {
            renderLoading(I18n.format("blockrenderer.gui.renderCancelled"), I18n.format("blockrenderer.gui.progress", rendered, total, (total - rendered)), (float)rendered/ total);
            addMessage(new TranslationTextComponent("msg.blockrenderer.bulk.cancelled", rendered, joined, asClickable(folder), total));
        }

        addMessage(new TranslationTextComponent("msg.blockrenderer.bulk.time", elapsed / 1000f));

        renderer.teardown();

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        client.displayGuiScreen(null);
    }

    private static void renderLoading(String title, String subtitle, float progress) {
        renderLoading(title, subtitle, null, progress);
    }

    private static void renderLoading(String title, String subtitle, @Nullable ItemStack is, float progress) {
        client.getFramebuffer().unbindFramebuffer();
        GlStateManager.pushMatrix();

        {
            int displayWidth = window.getScaledWidth();
            int displayHeight = window.getScaledHeight();
            Rendering.setupOverlayRendering();

            // Draw the dirt background
            Rendering.drawBackground(displayWidth, displayHeight);

            // ...and the title
            Rendering.drawCenteredString(client.fontRenderer, title, displayWidth / 2, displayHeight / 2 - 24, -1);

            // ...and the progress bar
            Rendering.drawRect(displayWidth / 2 - 50, displayHeight / 2 - 1, displayWidth / 2 + 50, displayHeight / 2 + 1, 0xFF001100);
            Rendering.drawRect(displayWidth / 2 - 50, displayHeight / 2 - 1, (displayWidth / 2 - 50) + (int) (progress * 100), displayHeight / 2 + 1, 0xFF55FF55);

            GlStateManager.pushMatrix();

            {
                GlStateManager.scalef(0.5f, 0.5f, 1);

                // ...and the subtitle
                Rendering.drawCenteredString(client.fontRenderer, subtitle, displayWidth, displayHeight - 20, -1);

                // ...and the tooltip.
                if (is != null) {
                    try {
                        List<String> list = getTooltipFromItem(is);

                        // This code is copied from the tooltip renderer, so we can properly center it.
                        FontRenderer font = is.getItem().getFontRenderer(is);
                        if (font == null) font = client.fontRenderer;

                        int width = 0;
                        for (String s : list) {
                            int j = font.getStringWidth(s);
                            if (j > width) width = j;
                        }
                        // End copied code.

                        GlStateManager.translatef((displayWidth - width / 2f) - 12, displayHeight + 30, 0);
                        Rendering.drawHoveringText(list, 0, 0, font);
                    } catch (Throwable ignored) {}
                }
            }

            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();

        client.updateDisplay(false);

        /*
         * While OpenGL itself is double-buffered, Minecraft is actually *triple*-buffered.
         * This is to allow shaders to work, as shaders are only available in "modern" GL.
         * Minecraft uses "legacy" GL, so it renders using a separate GL context to this
         * third buffer, which is then flipped to the back buffer with this call.
         */
        client.getFramebuffer().bindFramebuffer(false);
    }

}
