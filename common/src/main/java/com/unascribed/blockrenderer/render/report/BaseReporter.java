package com.unascribed.blockrenderer.render.report;

import com.unascribed.blockrenderer.varia.Maths;
import com.unascribed.blockrenderer.varia.debug.Debug;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import com.unascribed.blockrenderer.varia.rendering.DisplayI;
import com.unascribed.blockrenderer.varia.rendering.GLI;
import org.apache.logging.log4j.message.MessageFormatMessage;
import org.jetbrains.annotations.Nullable;

public abstract class BaseReporter<Component> {

    private static final int DARK_GREEN = 0xFF001100;
    private static final int LIGHT_GREEN = 0xFF55FF55;

    private final DisplayI<Component> Display;
    private final GLI GL;

    public final Component defaultTitle;
    public Component title;

    @Nullable
    public Component message = null;

    public int steps = -1;
    public int step = 0;

    private long start;
    private long last;

    public BaseReporter(DisplayI<Component> display, GLI gl, Component title) {
        this.Display = display;
        this.GL = gl;
        this.defaultTitle = title;
        this.title = title;
    }

    public void init(Component title, int steps) {
        reset();

        this.title = title;
        this.steps = steps;
        start = System.nanoTime();
        last = start;
    }

    public void push(@Nullable Component message) {
        this.message = message;
        step++;

        if (steps >= 0 && step > steps) Log.warn(Markers.PROGRESS, "Too many steps");
    }

    public abstract void pop();

    protected void pop(String title, String subtitle) {
        /* Log Time */
        long now = System.nanoTime();
        float time = (now - last) / 1_000_000_000F;
        Log.debug(Markers.PROGRESS, new MessageFormatMessage("Step: {0} - {1} took {2,number,#.###}s", title, subtitle, time));
        last = now;
    }

    public abstract void end();

    protected void end(String title, String subtitle) {
        /* Log Time */
        long now = System.nanoTime();
        if (start != 0) {
            float time = (now - start) / 1_000_000_000F;
            Log.debug(Markers.PROGRESS, new MessageFormatMessage("Finished: {0} - {1} took {2,number,#.###}s", title, subtitle, time));
        }

        reset();
    }

    public void skip() {
        push(getProgress());
        pop();
    }

    public void reset() {
        title = defaultTitle;
        message = null;

        steps = -1;
        step = 0;
        start = 0;
    }

    public abstract Component getProgress();

    public String getProgressString() {
        //TODO: Rendered, Total, Remaining + Elapsed Time
        if (steps > 0) return String.format("%s / %s", step + 1, steps);

        return String.format("%s", step);
    }

    public void render() {
        Debug.endFrame();
        Debug.push("progress-bar");

        GL.unbindFBO();

        GL.pushMatrix("progress/main");

        int displayWidth = GL.getScaledWidth();
        int displayHeight = GL.getScaledHeight();
        GL.setupOverlayRendering();

        // Draw the dirt background
        Display.drawDirtBackground(displayWidth, displayHeight);

        // ...and the title
        Display.drawCenteredString(title, displayWidth / 2, displayHeight / 2 - 24, -1);

        // ...and the progress bar
        renderProgressBar(displayWidth, displayHeight);

        if (message != null) {
            GL.pushMatrix("progress/message");

            GL.scale(0.5f, 0.5f, 1);

            // ...and the subtitle
            Display.drawCenteredString(message, displayWidth, displayHeight - 20, -1);

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

        Display.drawRect(hw - 50, hh - 1, hw + 50, hh + 1, DARK_GREEN);
        Display.drawRect(hw - 50, hh - 1, hw - 50 + progress, hh + 1, LIGHT_GREEN);
    }

}
