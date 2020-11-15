package com.unascribed.blockrenderer.render.report;

import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import org.apache.logging.log4j.message.MessageFormatMessage;
import org.jetbrains.annotations.Nullable;

public abstract class BaseReporter<Component> {

    public final Component defaultTitle;
    public Component title;

    @Nullable
    public Component message = null;

    public int steps = -1;
    public int step = 0;

    private long start;
    private long last;

    public BaseReporter(Component title) {
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

    public abstract void render();
}
