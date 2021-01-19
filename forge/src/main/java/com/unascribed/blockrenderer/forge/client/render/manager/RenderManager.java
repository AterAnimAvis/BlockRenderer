package com.unascribed.blockrenderer.forge.client.render.manager;

import com.unascribed.blockrenderer.forge.client.render.report.Reporter;
import com.unascribed.blockrenderer.render.IRequest;
import com.unascribed.blockrenderer.render.manager.BaseRenderManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.Nullable;

import java.util.PriorityQueue;
import java.util.Queue;

import static com.unascribed.blockrenderer.forge.client.varia.Strings.rawText;

public class RenderManager extends BaseRenderManager<ITextComponent> {

    private static final ITextComponent RENDERING_BULK = rawText("Rendering Bulk").mergeStyle(TextFormatting.GOLD);
    private static final ITextComponent RENDERING_GIF = rawText("Rendering GIF").mergeStyle(TextFormatting.GOLD);
    private static final ITextComponent RENDERING_AUTO = rawText("Auto Loop").mergeStyle(TextFormatting.GOLD);
    private static final ITextComponent RENDERING_SKIP = rawText("Skipping First").mergeStyle(TextFormatting.GOLD);

    public static final BaseRenderManager<ITextComponent> INSTANCE = new RenderManager();

    public static boolean isRendering = false;
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

        isRendering = false;
    }

    private RenderManager() {
        super(Reporter.INSTANCE, RENDERING_BULK, RENDERING_GIF, RENDERING_AUTO, RENDERING_SKIP);
    }
}
