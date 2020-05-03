package com.unascribed.blockrenderer.render;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.blockrenderer.lib.TileRenderer;
import com.unascribed.blockrenderer.utils.ImageUtils;
import com.unascribed.blockrenderer.utils.Rendering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
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

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Window window = client.getWindow();
    private static final ItemRenderer itemRenderer = client.getItemRenderer();

    private float oldZLevel;

    private TileRenderer renderer;

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

    public void render(ItemStack stack) {
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
                itemRenderer.renderGuiItem(stack, 0, 0);

            RenderSystem.popMatrix();
        } while (renderer.endTile());

        /* Reset nano supplier */
        Util.nanoTimeSupplier = oldSupplier;

    }

    @SuppressWarnings("UnstableApiUsage")
    public Text save(File folder, String filename) {
        try {
            BufferedImage img = ImageUtils.readPixels(renderer);

            File file = getFile(folder, filename);
            Files.createParentDirs(file);

            ImageIO.write(img, "PNG", file);
            return getRenderSuccess(folder, file);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new TranslatableText("msg.blockrenderer.render.fail");
        }
    }

    public void teardown() {
        /* Reset zLevel */
        itemRenderer.zOffset = oldZLevel;

        /* Reset Culling */
        GL11.glCullFace(GL11.GL_BACK);

        /* Pop Stack */
        RenderSystem.popMatrix();
    }

    public static void renderItem(int size, ItemStack stack, boolean useId, boolean addSize) {
        ItemStackRenderer renderer = new ItemStackRenderer();

        String sizeString = addSize ? size + "x" + size + "_" : "";
        String fileName = useId ? sanitize(Registry.ITEM.getId(stack.getItem()).toString()) : sanitize(stack.getName());

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
            addMessage(new TranslatableText("msg.blockrenderer.bulk.noItems", joined));
            return;
        }

        client.openScreen(new GameMenuScreen(false));

        int rendered = 0;
        long lastUpdate = 0;
        int total = renders.size();

        String sizeString = addSize ? size + "x" + size + "_" : "";
        File folder = new File(DEFAULT_FOLDER, dateTime() + "_" + sizeString + sanitize(namespaceSpec) + "/");
        String title = I18n.translate("blockrenderer.gui.rendering", total, joined);

        long start = Util.getMeasuringTimeMs();

        ItemStackRenderer renderer = new ItemStackRenderer();

        renderer.setup(size);
        for (ItemStack stack : renders) {
            if (isEscapePressed()) break;

            String fileName = useId ? sanitize(Registry.ITEM.getId(stack.getItem()).toString()) : sanitize(stack.getName());

            renderer.render(stack);
            renderer.save(folder, fileName);
            rendered++;

            if (Util.getMeasuringTimeMs() - lastUpdate > 33) {
                renderer.teardown();

                renderLoading(title, I18n.translate("blockrenderer.gui.progress", rendered, total, (total - rendered)), stack, (float)rendered/ total);

                lastUpdate = Util.getMeasuringTimeMs();
                renderer.setup(size);
            }
        }

        long elapsed = Util.getMeasuringTimeMs() - start;

        if (rendered >= total) {
            renderLoading(I18n.translate("blockrenderer.gui.rendered", total, joined), "", 1f);
            addMessage(new TranslatableText("msg.blockrenderer.bulk.finished", total, joined, asClickable(folder)));
        } else {
            renderLoading(I18n.translate("blockrenderer.gui.renderCancelled"), I18n.translate("blockrenderer.gui.progress", rendered, total, (total - rendered)), (float)rendered/ total);
            addMessage(new TranslatableText("msg.blockrenderer.bulk.cancelled", rendered, joined, asClickable(folder), total));
        }

        addMessage(new TranslatableText("msg.blockrenderer.bulk.time", elapsed / 1000f));

        renderer.teardown();

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        client.openScreen(null);
    }

    private static void renderLoading(String title, String subtitle, float progress) {
        renderLoading(title, subtitle, null, progress);
    }

    private static void renderLoading(String title, String subtitle, @Nullable ItemStack is, float progress) {
        client.getFramebuffer().endWrite();
        RenderSystem.pushMatrix();

        MatrixStack matrixStack = new MatrixStack();

        {
            int displayWidth = window.getScaledWidth();
            int displayHeight = window.getScaledHeight();
            Rendering.setupOverlayRendering();

            // Draw the dirt background
            Rendering.drawBackground(displayWidth, displayHeight);

            // ...and the title
            Rendering.drawCenteredString(matrixStack, client.textRenderer, title, displayWidth / 2, displayHeight / 2 - 24, -1);

            // ...and the progress bar
            Rendering.drawRect(matrixStack, displayWidth / 2 - 50, displayHeight / 2 - 1, displayWidth / 2 + 50, displayHeight / 2 + 1, 0xFF001100);
            Rendering.drawRect(matrixStack, displayWidth / 2 - 50, displayHeight / 2 - 1, (displayWidth / 2 - 50) + (int) (progress * 100), displayHeight / 2 + 1, 0xFF55FF55);

            RenderSystem.pushMatrix();

            {
                RenderSystem.scalef(0.5f, 0.5f, 1);

                // ...and the subtitle
                Rendering.drawCenteredString(matrixStack, client.textRenderer, subtitle, displayWidth, displayHeight - 20, -1);

                // ...and the tooltip.
                if (is != null) {
                    try {
                        List<Text> list = getTooltipFromItem(is);

                        // This code is copied from the tooltip renderer, so we can properly center it.
                        TextRenderer font = client.textRenderer;

                        int width = 0;
                        for (Text s : list) {
                            int j = font.getStringWidth(s);
                            if (j > width) width = j;
                        }
                        // End copied code.

                        RenderSystem.translatef((displayWidth - width / 2f) - 12, displayHeight + 30, 0);
                        Rendering.drawHoveringText(matrixStack, list, 0, 0);
                    } catch (Throwable ignored) {}
                }
            }

            RenderSystem.popMatrix();
        }

        RenderSystem.popMatrix();

        window.swapBuffers();

        /*
         * While OpenGL itself is double-buffered, Minecraft is actually *triple*-buffered.
         * This is to allow shaders to work, as shaders are only available in "modern" GL.
         * Minecraft uses "legacy" GL, so it renders using a separate GL context to this
         * third buffer, which is then flipped to the back buffer with this call.
         */
        client.getFramebuffer().beginWrite(false);
    }

}
