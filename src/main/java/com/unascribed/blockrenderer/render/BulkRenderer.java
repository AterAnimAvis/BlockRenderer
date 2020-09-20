package com.unascribed.blockrenderer.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.blockrenderer.utils.Rendering;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

import static com.unascribed.blockrenderer.render.IRenderer.DEFAULT_FOLDER;
import static com.unascribed.blockrenderer.utils.MiscUtils.isEscapePressed;
import static com.unascribed.blockrenderer.utils.StringUtils.*;

public class BulkRenderer {

    private static final int WAIT = 1500;

    private static final Minecraft client = Minecraft.getInstance();
    private static final MainWindow window = client.mainWindow;

    public static <T> void bulkRender(IRenderer<T> renderer, String spec, List<T> renders, int size, boolean useId, boolean addSize) {
        if (renders.size() < 1) {
            addMessage(new TranslationTextComponent("msg.block_renderer.bulk.noItems", spec));
            return;
        }

        client.displayGuiScreen(new IngameMenuScreen(false));

        int errored = 0;
        int rendered = 0;
        long lastUpdate = 0;
        int total = renders.size();

        String sizeString = addSize ? size + "x" + size + "_" : "";
        File folder = new File(DEFAULT_FOLDER, dateTime() + "_" + sizeString + sanitize(spec) + "/");
        String title = I18n.format("block_renderer.gui.rendering", total, spec);

        long start = Util.milliTime();

        renderer.setup(size);
        for (T value : renders) {
            if (isEscapePressed()) break;

            String fileName = renderer.getFilename(value, useId);

            try {
                renderer.render(value);
                renderer.saveRaw(folder, fileName);
            } catch (Exception e) {
                System.err.println("Rendering: " + renderer.getId(value));
                e.printStackTrace();
                errored++;
            }

            rendered++;

            if (Util.milliTime() - lastUpdate > 33) {
                renderer.teardown();

                renderLoading(renderer, title, I18n.format("block_renderer.gui.progress", rendered, total, (total - rendered)), value, (float)rendered/ total);

                lastUpdate = Util.milliTime();
                renderer.setup(size);
            }
        }

        long elapsed = Util.milliTime() - start;

        if (rendered >= total) {
            renderLoading(renderer, I18n.format("block_renderer.gui.rendered", total, spec), "", null, 1f);
            addMessage(new TranslationTextComponent("msg.block_renderer.bulk.finished", total, spec, asClickable(folder)));
        } else {
            renderLoading(renderer, I18n.format("block_renderer.gui.renderCancelled"), I18n.format("block_renderer.gui.progress", rendered, total, (total - rendered)), null, (float)rendered/ total);
            addMessage(new TranslationTextComponent("msg.block_renderer.bulk.cancelled", rendered, spec, asClickable(folder), total));
        }

        if (errored > 0) {
            addMessage("");
            addMessage(I18n.format("msg.block_renderer.bulk.errored", errored));
        }

        addMessage("");
        addMessage(new TranslationTextComponent("msg.block_renderer.bulk.time", elapsed / 1000f));

        renderer.teardown();

        try { Thread.sleep(WAIT); } catch (InterruptedException ignored) {}

        client.displayGuiScreen(null);
    }

    private static <T> void renderLoading(IRenderer<T> renderer, String title, String subtitle, @Nullable T value, float progress) {
        client.getFramebuffer().unbindFramebuffer();

        GlStateManager.pushMatrix();

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

                GlStateManager.scalef(0.5f, 0.5f, 1);

                // ...and the subtitle
                Rendering.drawCenteredString(client.fontRenderer, subtitle, displayWidth, displayHeight - 20, -1);

                // ...and the tooltip.
                if (value != null) renderer.renderTooltip(value, displayWidth, displayHeight);

            GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        client.updateDisplay(false);

        client.getFramebuffer().bindFramebuffer(false);
    }

}