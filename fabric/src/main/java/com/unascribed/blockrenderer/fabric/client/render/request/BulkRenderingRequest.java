package com.unascribed.blockrenderer.fabric.client.render.request;

import com.unascribed.blockrenderer.fabric.client.render.report.ReporterBulk;
import com.unascribed.blockrenderer.fabric.client.screens.progress.ProgressLoader;
import com.unascribed.blockrenderer.render.ILoader;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.render.request.BaseBulkRenderingRequest;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class BulkRenderingRequest<S, T> extends BaseBulkRenderingRequest<S, T, ITextComponent> {

    private final Executor BACKGROUND = Util.getServerExecutor();

    public BulkRenderingRequest(IRenderer<S, T> renderer, S parameters, String name, Collection<T> values, Function<T, ITextComponent> asDisplayName, ImageHandler<T> handler, Runnable callback) {
        super(new ReporterBulk(name, values.size()), renderer, parameters, name, values, asDisplayName, handler, callback);
    }

    @Override
    protected ILoader createAndInstallLoader() {
        ProgressLoader<S, T> overlay = new ProgressLoader<>(this, reporter);
        Minecraft.getInstance().setLoadingGui(overlay);
        reporter.init();
        return overlay;
    }

    @Override
    protected Executor getBackgroundExecutor() {
        return BACKGROUND;
    }

}
