package com.unascribed.blockrenderer.render.report;

import com.unascribed.blockrenderer.varia.Colors;
import com.unascribed.blockrenderer.varia.Maths;
import com.unascribed.blockrenderer.varia.Time;
import com.unascribed.blockrenderer.varia.debug.Debug;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import com.unascribed.blockrenderer.varia.rendering.DisplayI;
import com.unascribed.blockrenderer.varia.rendering.GLI;
import org.apache.logging.log4j.message.MessageFormatMessage;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseReporterBulk<Component> {

    private final DisplayI<Component> Display;
    private final GLI GL;

    public final String name;
    public Component title;
    public List<Component> subTitles = new ArrayList<>();

    public int steps;

    public int render = 0;
    public int finished = 0;

    private long start;
    private long last;

    private boolean cancelled;

    public BaseReporterBulk(DisplayI<Component> display, GLI gl, String name, Component title, int total) {
        this.Display = display;
        this.GL = gl;
        this.name = name;
        this.title = title;
        this.steps = total;
    }

    public void init() {
        start = System.nanoTime();
        last = start;
    }

    public void increment() {
        finished++;
    }

    protected abstract Component getCancelled();

    public void cancel() {
        cancelled = true;
        title = getCancelled();
        subTitles.clear();
    }

    protected abstract Component getProgress(int rendered, int total, int remaining);

    public void update(Component displayName) { // TODO: This is based on Rendered Values not finished
        render++;

        /* Log Time */
        long now = System.nanoTime();
        float time = (now - last) / Time.NANOS_IN_A_SECOND_F;
        Log.debug(Markers.PROGRESS, new MessageFormatMessage("Step: {0} - {1} took {2,number,#.###}s", title, displayName, time));
        last = now;

        int remaining = steps - render;
        if (remaining <= 0) { //TODO: We probably just want to return here
            complete();
            return;
        }

        if (cancelled) {
            cancel();
            return;
        }

        subTitles.clear();
        subTitles.add(getProgress(render, steps, remaining));
        subTitles.add(displayName);
    }

    protected abstract Component getFinished(String name, int total);

    public void complete() {
        title = getFinished(name, steps);
        subTitles.clear();

        /* Log Time */
        long now = System.nanoTime();
        if (start != 0) {
            float time = (now - start) / Time.NANOS_IN_A_SECOND_F;
            Log.debug(Markers.PROGRESS, new MessageFormatMessage("Finished: {0} took {2,number,#.###}s", name, time));
            start = 0L;
        }
    }

    public void render(float alpha) {
        int a = Maths.ceil(alpha * 255);

        Debug.endFrame();
        Debug.push("progress-bar");

        GL.pushMatrix("progress/main");

        int displayWidth = GL.getScaledWidth();
        int displayHeight = GL.getScaledHeight();

        // Draw the dirt background
        Display.drawDirtBackground(displayWidth, displayHeight, alpha);

        // ...and the title
        Display.drawCenteredString(title, displayWidth / 2, displayHeight / 2 - 24, 0xFFFFFF | a);

        // ...and the progress bar
        renderProgressBar(displayWidth, displayHeight, a);

        int subTitlesCount = subTitles.size();
        if (subTitlesCount > 0) {
            GL.pushMatrix("progress/message");

            GL.scale(0.5f, 0.5f, 1);

            // ...and the subtitle
            if (alpha != 0f) {
                for (int i = 0; i < subTitlesCount; i++) {
                    Display.drawCenteredString(subTitles.get(i), displayWidth, displayHeight + (20 * (i + 1)), 0xFFFFFF | a);
                }
            }

            GL.popMatrix("progress/message");
        }

        GL.popMatrix("progress/main");
        Debug.pop();
    }

    private void renderProgressBar(int displayWidth, int displayHeight, int alpha) {
        int progress = steps > 0 ? Maths.clamp(100 * finished / steps, 0, 100) : 100;

        int hw = displayWidth / 2;
        int hh = displayHeight / 2;

        Display.drawRect(hw - 50, hh - 1, hw + 50, hh + 1, Colors.DARK_GREEN | alpha << 24);
        Display.drawRect(hw - 50, hh - 1, hw - 50 + progress, hh + 1, Colors.LIGHT_GREEN | alpha << 24);
    }

}