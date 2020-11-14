package com.unascribed.blockrenderer.forge.client.render.report;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.forge.client.varia.rendering.Display;
import com.unascribed.blockrenderer.forge.client.varia.rendering.GL;
import com.unascribed.blockrenderer.varia.Time;
import com.unascribed.blockrenderer.varia.debug.Debug;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.message.MessageFormatMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProgressManager {

    private static final int DARK_GREEN = 0xFF001100;
    private static final int LIGHT_GREEN = 0xFF55FF55;

    @NotNull
    public static ITextComponent title = new StringTextComponent("Rendering");
    @Nullable
    public static ITextComponent message = null;

    public static int steps = -1;
    public static int step = 0;

    private static long start;
    private static long last;

    private static long lastRender = System.nanoTime();

    public static void init(ITextComponent title, int steps) {
        reset();

        ProgressManager.title = title;
        ProgressManager.steps = steps;
        start = System.nanoTime();
        last = start;
    }

    public static void push(@Nullable ITextComponent message) {
        ProgressManager.message = message;

        step++;

        if (steps >= 0 && step > steps) Log.warn(Markers.PROGRESS, "Too many steps");
    }

    public static void pop() {
        /* Log Time */
        long now = System.nanoTime();
        float time = (now - last) / 1_000_000_000F;
        String subtitle = message == null ? "null" : message.getString();
        Log.debug(Markers.PROGRESS, new MessageFormatMessage("Step: {0} - {1} took {2,number,#.###}s", title.getString(), subtitle, time));
        last = now;
    }

    public static void end() {
        /* Log Time */
        long now = System.nanoTime();
        if (start != 0) {
            float time = (now - start) / 1_000_000_000F;
            String subtitle = message == null ? "null" : message.getString();
            Log.debug(Markers.PROGRESS, new MessageFormatMessage("Finished: {0} - {1} took {2,number,#.###}s", title.getString(), subtitle, time));
        }

        reset();
    }

    public static void reset() {
        title = new StringTextComponent("Rendering");
        message = null;
        steps = -1;
        step = 0;
        start = 0;
    }

    public static ITextComponent getProgress() {
        //TODO: Rendered, Total, Remaining + Elapsed Time
        if (steps > 0) return new StringTextComponent(String.format("%s / %s", step + 1, steps));

        return new StringTextComponent(String.format("%s", step));
    }

    public static void render() {
        if (System.nanoTime() - lastRender < (Time.NANOS_PER_FRAME)) return;
        lastRender = System.nanoTime();

        Debug.endFrame();
        Debug.push("progress-bar");

        GL.unbindFBO();

        GL.pushMatrix("progress/main");

        int displayWidth = GL.window.getGuiScaledWidth();
        int displayHeight = GL.window.getGuiScaledHeight();
        GL.setupOverlayRendering();

        // Draw the dirt background
        Display.drawDirtBackground(displayWidth, displayHeight);

        // ...and the title
        Display.drawCenteredString(new MatrixStack(), ProgressManager.title, displayWidth / 2, displayHeight / 2 - 24, -1);

        // ...and the progress bar
        renderProgressBar(displayWidth, displayHeight);

        if (message != null) {
            GL.pushMatrix("progress/message");

            GL.scale(0.5f, 0.5f, 1);

            // ...and the subtitle
            Display.drawCenteredString(new MatrixStack(), message, displayWidth, displayHeight - 20, -1);

            GL.popMatrix("progress/message");
        }

        GL.popMatrix("progress/main");

        GL.flipFrame();
        GL.rebindFBO();

        Debug.pop();
    }

    private static void renderProgressBar(int displayWidth, int displayHeight) {
        int progress = steps > 0 ? MathHelper.clamp(100 * step / steps, 0, 100) : 100;

        int hw = displayWidth / 2;
        int hh = displayHeight / 2;

        Display.drawRect(new MatrixStack(), hw - 50, hh - 1, hw + 50, hh + 1, DARK_GREEN);
        Display.drawRect(new MatrixStack(), hw - 50, hh - 1, hw - 50 + progress, hh + 1, LIGHT_GREEN);
    }

}
