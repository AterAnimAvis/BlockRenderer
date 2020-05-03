package com.unascribed.blockrenderer.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.blockrenderer.utils.Rendering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

import static com.unascribed.blockrenderer.render.IRenderer.DEFAULT_FOLDER;
import static com.unascribed.blockrenderer.utils.MiscUtils.isEscapePressed;
import static com.unascribed.blockrenderer.utils.StringUtils.*;

public class BulkRenderer {

    private static final int WAIT = 1500;

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Window window = client.getWindow();

    public static <T> void bulkRender(IRenderer<T> renderer, String spec, List<T> renders, int size, boolean useId, boolean addSize) {
        if (renders.size() < 1) {
            addMessage(new TranslatableText("msg.blockrenderer.bulk.noItems", spec));
            return;
        }

        client.openScreen(new GameMenuScreen(false));

        int errored = 0;
        int rendered = 0;
        long lastUpdate = 0;
        int total = renders.size();

        String sizeString = addSize ? size + "x" + size + "_" : "";
        File folder = new File(DEFAULT_FOLDER, dateTime() + "_" + sizeString + sanitize(spec) + "/");
        String title = I18n.translate("blockrenderer.gui.rendering", total, spec);

        long start = Util.getMeasuringTimeMs();

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

            if (Util.getMeasuringTimeMs() - lastUpdate > 33) {
                renderer.teardown();

                renderLoading(renderer, title, I18n.translate("blockrenderer.gui.progress", rendered, total, (total - rendered)), value, (float)rendered/ total);

                lastUpdate = Util.getMeasuringTimeMs();
                renderer.setup(size);
            }
        }

        long elapsed = Util.getMeasuringTimeMs() - start;

        if (rendered >= total) {
            renderLoading(renderer, I18n.translate("blockrenderer.gui.rendered", total, spec), "", null, 1f);
            addMessage(new TranslatableText("msg.blockrenderer.bulk.finished", total, spec, asClickable(folder)));
        } else {
            renderLoading(renderer, I18n.translate("blockrenderer.gui.renderCancelled"), I18n.translate("blockrenderer.gui.progress", rendered, total, (total - rendered)), null, (float)rendered/ total);
            addMessage(new TranslatableText("msg.blockrenderer.bulk.cancelled", rendered, spec, asClickable(folder), total));
        }

        if (errored > 0) addMessage(I18n.translate("msg.blockrenderer.bulk.errored", errored));

        addMessage(new TranslatableText("msg.blockrenderer.bulk.time", elapsed / 1000f));

        renderer.teardown();

        try { Thread.sleep(WAIT); } catch (InterruptedException ignored) {}

        client.openScreen(null);
    }

    private static <T> void renderLoading(IRenderer<T> renderer, String title, String subtitle, @Nullable T value, float progress) {
        client.getFramebuffer().endWrite();

        RenderSystem.pushMatrix();
        MatrixStack stack = new MatrixStack();

            int displayWidth = window.getScaledWidth();
            int displayHeight = window.getScaledHeight();
            Rendering.setupOverlayRendering();

            // Draw the dirt background
            Rendering.drawBackground(displayWidth, displayHeight);

            // ...and the title
            Rendering.drawCenteredString(stack, client.textRenderer, title, displayWidth / 2, displayHeight / 2 - 24, -1);

            // ...and the progress bar
            Rendering.drawRect(stack, displayWidth / 2 - 50, displayHeight / 2 - 1, displayWidth / 2 + 50, displayHeight / 2 + 1, 0xFF001100);
            Rendering.drawRect(stack, displayWidth / 2 - 50, displayHeight / 2 - 1, (displayWidth / 2 - 50) + (int) (progress * 100), displayHeight / 2 + 1, 0xFF55FF55);

            RenderSystem.pushMatrix();

                RenderSystem.scalef(0.5f, 0.5f, 1);

                // ...and the subtitle
                Rendering.drawCenteredString(stack, client.textRenderer, subtitle, displayWidth, displayHeight - 20, -1);

                // ...and the tooltip.
                if (value != null) renderer.renderTooltip(stack, value, displayWidth, displayHeight);

            RenderSystem.popMatrix();

        RenderSystem.popMatrix();

        window.swapBuffers();

        client.getFramebuffer().beginWrite(false);
    }

}
