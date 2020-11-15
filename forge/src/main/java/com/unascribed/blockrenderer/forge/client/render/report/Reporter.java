package com.unascribed.blockrenderer.forge.client.render.report;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.forge.client.varia.rendering.Display;
import com.unascribed.blockrenderer.forge.client.varia.rendering.GL;
import com.unascribed.blockrenderer.render.report.BaseReporter;
import com.unascribed.blockrenderer.varia.Maths;
import com.unascribed.blockrenderer.varia.Time;
import com.unascribed.blockrenderer.varia.debug.Debug;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class Reporter extends BaseReporter<ITextComponent> {

    private static final int DARK_GREEN = 0xFF001100;
    private static final int LIGHT_GREEN = 0xFF55FF55;

    public static Reporter INSTANCE = new Reporter();

    private static long lastRender = System.nanoTime();

    private Reporter() {
        super(new StringTextComponent("Rendering"));
    }

    @Override
    public void pop() {
        pop(title.getString(), message == null ? "null" : message.getString());
    }

    @Override
    public void end() {
        end(title.getString(), message == null ? "null" : message.getString());
    }

    @Override
    public ITextComponent getProgress() {
        return new StringTextComponent(getProgressString());
    }

    @Override
    public void render() {
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
        Display.drawCenteredString(new MatrixStack(), title, displayWidth / 2, displayHeight / 2 - 24, -1);

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

    private void renderProgressBar(int displayWidth, int displayHeight) {
        int progress = steps > 0 ? Maths.clamp(100 * step / steps, 0, 100) : 100;

        int hw = displayWidth / 2;
        int hh = displayHeight / 2;

        Display.drawRect(new MatrixStack(), hw - 50, hh - 1, hw + 50, hh + 1, DARK_GREEN);
        Display.drawRect(new MatrixStack(), hw - 50, hh - 1, hw - 50 + progress, hh + 1, LIGHT_GREEN);
    }

}
