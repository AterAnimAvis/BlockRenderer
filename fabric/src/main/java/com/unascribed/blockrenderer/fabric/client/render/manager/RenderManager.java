package com.unascribed.blockrenderer.fabric.client.render.manager;

import com.unascribed.blockrenderer.fabric.client.render.report.Reporter;
import com.unascribed.blockrenderer.render.IRequest;
import com.unascribed.blockrenderer.render.manager.BaseRenderManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;

import static com.unascribed.blockrenderer.fabric.client.varia.Strings.translate;

public class RenderManager extends BaseRenderManager<ITextComponent> {

    private static final Function<String, ITextComponent> RENDERING_BULK = (name) -> translate("block_renderer.render.bulk", name).withStyle(TextFormatting.GOLD);
    private static final ITextComponent RENDERING_GIF = translate("block_renderer.render.gif").withStyle(TextFormatting.GOLD);
    private static final ITextComponent RENDERING_AUTO = translate("block_renderer.render.auto_loop").withStyle(TextFormatting.GOLD);
    private static final ITextComponent RENDERING_SKIP = translate("block_renderer.render.skip_frame").withStyle(TextFormatting.GOLD);

    public static final BaseRenderManager<ITextComponent> INSTANCE = new RenderManager();

    public static boolean isRendering = false;
    public static Queue<IRequest> requests = new PriorityQueue<>(Comparator.comparingInt(IRequest::priority));
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

        isRendering = false;
    }

    private RenderManager() {
        super(Reporter.INSTANCE, RENDERING_BULK, RENDERING_GIF, RENDERING_AUTO, RENDERING_SKIP);
    }
}
