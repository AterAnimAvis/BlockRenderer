package com.unascribed.blockrenderer.render.report;

import com.unascribed.blockrenderer.varia.Colors;
import com.unascribed.blockrenderer.varia.Maths;
import com.unascribed.blockrenderer.varia.Time;
import com.unascribed.blockrenderer.varia.debug.Debug;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import com.unascribed.blockrenderer.varia.rendering.DisplayI;
import org.apache.logging.log4j.message.MessageFormatMessage;
import org.jetbrains.annotations.Nullable;

import static com.unascribed.blockrenderer.Interop.GL;

public abstract class BaseReporter<Component> {

    private final DisplayI<Component> Display;

    public final Component defaultTitle;
    public Component title;

    @Nullable
    public Component message = null;

    public int steps = -1;
    public int step = 0;

    private long start;
    private long last;

    private long lastRender = System.nanoTime();

    public BaseReporter(Component title, DisplayI<Component> Display) {
        this.defaultTitle = title;
        this.title = title;
        this.Display = Display;
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
        float time = (now - last) / Time.NANOS_IN_A_SECOND_F;
        Log.debug(Markers.PROGRESS, new MessageFormatMessage("Step: {0} - {1} took {2,number,#.###}s", title, subtitle, time));
        last = now;
    }

    public abstract void end();

    protected void end(String title, String subtitle) {
        /* Log Time */
        long now = System.nanoTime();
        if (start != 0) {
            float time = (now - start) / Time.NANOS_IN_A_SECOND_F;
            Log.debug(Markers.PROGRESS, new MessageFormatMessage("Finished: {0} - {1} took {2,number,#.###}s", title, subtitle, time));
        }

        reset();
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
        if (System.nanoTime() - lastRender < (Time.NANOS_PER_FRAME)) return;
        lastRender = System.nanoTime();

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

        Display.drawRect(hw - 50, hh - 1, hw + 50, hh + 1, Colors.DARK_GREEN);
        Display.drawRect(hw - 50, hh - 1, hw - 50 + progress, hh + 1, Colors.LIGHT_GREEN);
    }

}
