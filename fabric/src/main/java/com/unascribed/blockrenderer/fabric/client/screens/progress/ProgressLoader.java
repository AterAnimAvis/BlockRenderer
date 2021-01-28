package com.unascribed.blockrenderer.fabric.client.screens.progress;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.blockrenderer.InternalAPI;
import com.unascribed.blockrenderer.fabric.client.render.manager.RenderManager;
import com.unascribed.blockrenderer.fabric.client.varia.MiscUtils;
import com.unascribed.blockrenderer.render.ILoader;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.render.report.BaseReporterBulk;
import com.unascribed.blockrenderer.render.request.BaseBulkRenderingRequest;
import com.unascribed.blockrenderer.varia.Time;
import com.unascribed.blockrenderer.varia.rendering.GLI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LoadingGui;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;

import static com.unascribed.blockrenderer.varia.Maths.clamp;

public class ProgressLoader<S, T> extends LoadingGui implements ILoader {

    private final BaseReporterBulk<ITextComponent> reporter;
    private final Minecraft mc = Minecraft.getInstance();

    private final BaseBulkRenderingRequest<S, T, ITextComponent> request;

    private long fadeOutStart = -1;
    private long fadeInStart = -1;

    private boolean cancelled;

    public ProgressLoader(BaseBulkRenderingRequest<S, T, ITextComponent> request, BaseReporterBulk<ITextComponent> reporter) {
        this.reporter = reporter;
        this.request = request;
    }

    @Override
    public boolean isCurrent() {
        return mc.getLoadingGui() == this;
    }

    @Override
    public void tick() {
        if (MiscUtils.isEscapePressed()) {
            cancel();
            return;
        }

        if (request.isRenderingFinished()) return;

        RenderManager.INSTANCE.isRendering = true;

        IRenderer<S, T> renderer = request.getRenderer();
        renderer.setup(request.getParameters());

        long start = Util.milliTime();
        tick:
        while (Util.milliTime() - start < Time.RENDER_TICK_MS) {
            /* Process at least 5 before we check the time again */
            for (int i = 0; i < 5; i++) { //TODO: Check Rough Speed Benefits
                /* No more to process */
                if (request.isRenderingFinished()) break tick;

                /* Otherwise Render the next Value */
                render(renderer, request.next());
            }
        }
        renderer.teardown();

        RenderManager.INSTANCE.isRendering = false;
    }

    private void render(IRenderer<S, T> renderer, T value) {
        renderer.render(value, request);
        reporter.update(request.getDisplayName(value));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        long now = Util.milliTime();

        /* Trigger Fade In */
        if (fadeInStart == -1) fadeInStart = now;

        /* Calculate Fade Timers */
        float fadeOutTime = fadeOutStart > -1 ? (now - fadeOutStart) / 1000f : -1;
        float fadeInTime = fadeInStart > -1 ? (now - fadeInStart) / 500f : -1;

        /* Calculate Alpha based on Fade Timer */
        float alpha = (fadeOutTime >= 1 ? 1 - clamp(fadeOutTime - 1, 0F, 1F) : clamp(fadeInTime, 0.15F, 1F));

        /* Render Underlying Screen if Fading In / Out */
        if (mc.currentScreen != null && (fadeOutTime >= 1 || fadeInTime < 1)) mc.currentScreen.render(stack, 0, 0, partialTicks);

        /* Render our Screen */
        reporter.render(alpha);

        /* Remove this Overlay if Fade Out is done */
        if (fadeOutTime >= 2) mc.setLoadingGui(null);

        /* Trigger Fade Out */
        if (fadeOutStart == -1 && request.isFinished() && fadeInTime >= 2) {
            request.complete();
            fadeOutStart = now;
            GLI GL = InternalAPI.getGL();
            if (mc.currentScreen != null) mc.currentScreen.init(mc, GL.getScaledWidth(), GL.getScaledHeight());
        }
    }

    private void cancel() {
        if (cancelled) return;
        cancelled = true;
        request.cancel();
    }

}
