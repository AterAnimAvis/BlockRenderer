package com.unascribed.blockrenderer.forge.client.render;

import com.unascribed.blockrenderer.forge.client.render.report.Reporter;
import com.unascribed.blockrenderer.render.BaseRenderManager;
import com.unascribed.blockrenderer.render.IRequest;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.PriorityQueue;
import java.util.Queue;

public class RenderManager extends BaseRenderManager<ITextComponent> {

    public static final BaseRenderManager<ITextComponent> INSTANCE = new RenderManager();

    private static final ITextComponent RENDERING_GIF = new TranslationTextComponent("block_renderer.render.gif").withStyle(TextFormatting.GOLD);
    private static final ITextComponent RENDERING_AUTO = new TranslationTextComponent("block_renderer.render.auto_loop").withStyle(TextFormatting.GOLD);

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
        return new TranslationTextComponent("block_renderer.render.bulk", name).withStyle(TextFormatting.GOLD);
    }
}
