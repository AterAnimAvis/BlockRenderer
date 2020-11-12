package com.unascribed.blockrenderer.varia.debug;

import com.unascribed.blockrenderer.Reference;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GREMEDYFrameTerminator;
import org.lwjgl.opengl.GREMEDYStringMarker;
import org.lwjgl.opengl.KHRDebug;

@SuppressWarnings("UnnecessaryReturnStatement")
public class Debug {

    private static final int SOURCE = KHRDebug.GL_DEBUG_SOURCE_APPLICATION;

    private static final boolean isKHREnabled = GL.getCapabilities().GL_KHR_debug;
    private static final boolean isGREMEDYEnabled = GL.getCapabilities().GL_GREMEDY_string_marker;
    private static final boolean isGREMEDYFrameEnabled = GL.getCapabilities().GL_GREMEDY_frame_terminator;

    public static void push(String group) {
        if (Reference.DEVELOPMENT)
            Log.debug(Markers.OPEN_GL_DEBUG, "Push Group '{}'", group);

        if (isKHREnabled) {
            KHRDebug.glPushDebugGroup(SOURCE, 0, Reference.ID + ":" + group);
            return;
        }

        if (isGREMEDYEnabled) {
            GREMEDYStringMarker.glStringMarkerGREMEDY(">> " + Reference.ID + ":" + group);
            return;
        }
    }

    public static void pop() {
        if (Reference.DEVELOPMENT)
            Log.debug(Markers.OPEN_GL_DEBUG, "Pop Group");

        if (isKHREnabled) {
            KHRDebug.glPopDebugGroup();
            return;
        }

        if (isGREMEDYEnabled) {
            GREMEDYStringMarker.glStringMarkerGREMEDY("<<");
            return;
        }
    }

    public static void endFrame() {
        if (Reference.DEVELOPMENT)
            Log.debug(Markers.OPEN_GL_DEBUG, "End Frame");

        if (isGREMEDYFrameEnabled) {
            GREMEDYFrameTerminator.glFrameTerminatorGREMEDY();
            return;
        }
    }


}
