package com.unascribed.blockrenderer;

import com.unascribed.blockrenderer.render.manager.IRenderManager;
import com.unascribed.blockrenderer.varia.rendering.GLI;
import org.jetbrains.annotations.Nullable;

public class InternalAPI {

    @Nullable
    private static IRenderManager RENDER_MANAGER;

    @Nullable
    private static GLI GL;

    public static IRenderManager getRenderManager() {
        if (RENDER_MANAGER == null) throw new AssertionError("Accessing IRenderManager before it was set");

        return RENDER_MANAGER;
    }

    public static void setRenderManager(IRenderManager manager) {
        RENDER_MANAGER = manager;
    }

    public static GLI getGL() {
        if (GL == null) throw new AssertionError("Accessing GLI before it was set");

        return GL;
    }

    public static void setGL(GLI gl) {
        GL = gl;
    }
}
