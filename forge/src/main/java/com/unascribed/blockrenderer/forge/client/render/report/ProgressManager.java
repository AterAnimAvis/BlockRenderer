package com.unascribed.blockrenderer.forge.client.render.report;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.forge.client.varia.rendering.Display;
import com.unascribed.blockrenderer.forge.client.varia.rendering.GL;
import com.unascribed.blockrenderer.render.report.BaseProgressManager;
import com.unascribed.blockrenderer.varia.Maths;
import com.unascribed.blockrenderer.varia.Time;
import com.unascribed.blockrenderer.varia.debug.Debug;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProgressManager {

    private static final int DARK_GREEN = 0xFF001100;
    private static final int LIGHT_GREEN = 0xFF55FF55;

    @NotNull
    public static ITextComponent title = new StringTextComponent("Rendering");
    @Nullable
    public static ITextComponent message = null;

    private static final BaseProgressManager MANAGER = new BaseProgressManager();

    private static long lastRender = System.nanoTime();

    public static void init(ITextComponent title, int steps) {
        reset();
        MANAGER.init(steps);

        ProgressManager.title = title;
    }

    public static void push(@Nullable ITextComponent message) {
        ProgressManager.message = message;
        MANAGER.push();
    }

    public static void pop() {
        MANAGER.pop(title.getString(), message == null ? "null" : message.getString());
    }

    public static void end() {
        MANAGER.end(title.getString(), message == null ? "null" : message.getString());

        reset();
    }

    private static void reset() {
        title = new StringTextComponent("Rendering");
        message = null;
    }

    public static ITextComponent getProgress() {
        return new StringTextComponent(MANAGER.getProgress());
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
        int progress = MANAGER.steps > 0 ? Maths.clamp(100 * MANAGER.step / MANAGER.steps, 0, 100) : 100;

        int hw = displayWidth / 2;
        int hh = displayHeight / 2;

        Display.drawRect(new MatrixStack(), hw - 50, hh - 1, hw + 50, hh + 1, DARK_GREEN);
        Display.drawRect(new MatrixStack(), hw - 50, hh - 1, hw - 50 + progress, hh + 1, LIGHT_GREEN);
    }

}
