package com.unascribed.blockrenderer.render.report;

import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import org.apache.logging.log4j.message.MessageFormatMessage;

public class BaseProgressManager {

    public int steps = -1;
    public int step = 0;

    private long start;
    private long last;

    public void init(int steps) {
        reset();

        this.steps = steps;
        start = System.nanoTime();
        last = start;
    }

    public void push() {
        step++;

        if (steps >= 0 && step > steps) Log.warn(Markers.PROGRESS, "Too many steps");
    }

    public void pop(String title, String subtitle) {
        /* Log Time */
        long now = System.nanoTime();
        float time = (now - last) / 1_000_000_000F;
        Log.debug(Markers.PROGRESS, new MessageFormatMessage("Step: {0} - {1} took {2,number,#.###}s", title, subtitle, time));
        last = now;
    }

    public void end(String title, String subtitle) {
        /* Log Time */
        long now = System.nanoTime();
        if (start != 0) {
            float time = (now - start) / 1_000_000_000F;
            Log.debug(Markers.PROGRESS, new MessageFormatMessage("Finished: {0} - {1} took {2,number,#.###}s", title, subtitle, time));
        }

        reset();
    }

    public void reset() {
        steps = -1;
        step = 0;
        start = 0;
    }

    public String getProgress() {
        //TODO: Rendered, Total, Remaining + Elapsed Time
        if (steps > 0) return String.format("%s / %s", step + 1, steps);

        return String.format("%s", step);
    }

}
