package com.unascribed.blockrenderer.fabric.client.render;

import com.unascribed.blockrenderer.fabric.client.render.report.Reporter;
import com.unascribed.blockrenderer.render.BaseRenderManager;
import com.unascribed.blockrenderer.render.IRequest;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.PriorityQueue;
import java.util.Queue;

public class RenderManager extends BaseRenderManager<Component> {

    public static final BaseRenderManager<Component> INSTANCE = new RenderManager();

    private static final Component RENDERING_GIF = new TranslatableComponent("block_renderer.render.gif").withStyle(ChatFormatting.GOLD);
    private static final Component RENDERING_AUTO = new TranslatableComponent("block_renderer.render.auto_loop").withStyle(ChatFormatting.GOLD);

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
        return new TranslatableComponent("block_renderer.render.bulk", name).withStyle(ChatFormatting.GOLD);
    }
}
