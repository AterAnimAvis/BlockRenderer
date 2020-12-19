package com.unascribed.blockrenderer.forge.client.render;

import com.unascribed.blockrenderer.forge.client.render.report.Reporter;
import com.unascribed.blockrenderer.forge.client.varia.Styles;
import com.unascribed.blockrenderer.render.IRequest;
import com.unascribed.blockrenderer.render.manager.BaseRenderManager;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.PriorityQueue;
import java.util.Queue;

import static com.unascribed.blockrenderer.forge.client.varia.Strings.translate;

public class RenderManager extends BaseRenderManager<ITextComponent> {

    private static final ITextComponent RENDERING_GIF = translate("block_renderer.render.gif").withStyle(Styles.GOLD);
    private static final ITextComponent RENDERING_AUTO = translate("block_renderer.render.auto_loop").withStyle(Styles.GOLD);

    public static final BaseRenderManager<ITextComponent> INSTANCE = new RenderManager();

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
    protected ITextComponent renderingBulk(String name) {
        return translate("block_renderer.render.bulk", name).withStyle(Styles.GOLD);
    }
}
