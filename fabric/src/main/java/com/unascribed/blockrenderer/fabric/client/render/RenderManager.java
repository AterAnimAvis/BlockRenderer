package com.unascribed.blockrenderer.fabric.client.render;

import com.unascribed.blockrenderer.fabric.client.render.report.Reporter;
import com.unascribed.blockrenderer.fabric.client.varia.Styles;
import com.unascribed.blockrenderer.render.BaseRenderManager;
import com.unascribed.blockrenderer.render.IRequest;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.PriorityQueue;
import java.util.Queue;

import static com.unascribed.blockrenderer.fabric.client.varia.StringUtils.translate;

public class RenderManager extends BaseRenderManager<Component> {

    private static final Component RENDERING_GIF = translate("block_renderer.render.gif").withStyle(Styles.GOLD);
    private static final Component RENDERING_AUTO = translate("block_renderer.render.auto_loop").withStyle(Styles.GOLD);

    public static final BaseRenderManager<Component> INSTANCE = new RenderManager();

    public static Queue<IRequest> requests = new PriorityQueue<>();
    @Nullable
    private static IRequest request = null;

    public static void push(IRequest request) {
        requests.add(request);
    }

    public static void onFrameStart() {
        if (request == null) request = requests.poll();
        if (request == null) return;

        if (request.render())
            request = null;

        INSTANCE.isRendering = false;
    }

    private RenderManager() {
        super(Reporter.INSTANCE, RENDERING_GIF, RENDERING_AUTO);
    }

    @Override
    protected Component renderingBulk(String name) {
        return translate("block_renderer.render.bulk", name).withStyle(Styles.GOLD);
    }
}
