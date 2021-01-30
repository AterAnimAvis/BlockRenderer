package com.unascribed.blockrenderer.render.request;

import com.unascribed.blockrenderer.render.ILoader;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.render.IRequest;
import com.unascribed.blockrenderer.render.report.BaseReporterBulk;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.rendering.STBWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public abstract class BaseBulkRenderingRequest<S, T, C> implements IRequest, ImageHandler<T> {

    @Nullable
    private ILoader loader;
    protected BaseReporterBulk<C> reporter;

    protected final IRenderer<S, T> renderer;
    protected final S parameters;
    protected final String name;
    protected final Collection<T> values;
    protected final Function<T, C> asDisplayName;
    protected final ImageHandler<T> handler;
    protected final Runnable callback;

    protected final List<CompletableFuture<Void>> pending = new LinkedList<>();
    protected final Iterator<T> iterator;

    protected boolean cancelled;

    public BaseBulkRenderingRequest(BaseReporterBulk<C> reporter, IRenderer<S, T> renderer, S parameters, String name, Collection<T> values, Function<T, C> asDisplayName, ImageHandler<T> handler, Runnable callback) {
        this.reporter = reporter;
        this.renderer = renderer;
        this.parameters = parameters;
        this.name = name;
        this.values = values;
        this.asDisplayName = asDisplayName;
        this.handler = handler;
        this.callback = callback;

        this.iterator = values.iterator();
    }

    /**
     * @return true when rendering has been completed
     */
    @Override
    public boolean render() {
        /* Set the current Loader to our Progress Reporter */
        if (loader == null) loader = createAndInstallLoader();

        /* If the current Loader isn't ours then we must of finished / cancelled so mark this Request as done */
        if (!loader.isCurrent()) return true;

        /* Process tick */
        loader.tick();

        /* Cut down on Memory by removing futures from the pending queue TODO: Check Rough Memory Benefits vs Performance */
        pending.removeIf(CompletableFuture::isDone);

        return false;
    }

    protected abstract ILoader createAndInstallLoader();

    protected abstract Executor getBackgroundExecutor();

    public void register(CompletableFuture<Void> future) {
        pending.add(future);
    }

    @Override
    public void accept(T value, STBWrapper image) {
        register(CompletableFuture.runAsync(() -> {
            handler.accept(value, image);
            reporter.increment();
        }, getBackgroundExecutor()));
    }

    public boolean isFinished() {
        return isRenderingFinished() && pending.stream().allMatch(CompletableFuture::isDone);
    }

    public IRenderer<S, T> getRenderer() {
        return renderer;
    }

    public S getParameters() {
        return parameters;
    }

    public boolean isRenderingFinished() {
        return cancelled || !iterator.hasNext();
    }

    public T next() {
        return iterator.next();
    }

    public void cancel() {
        cancelled = true;
        pending.forEach(future -> future.cancel(false));
        reporter.cancel();
    }

    public void complete() {
        reporter.complete();
        callback.run();
    }

    public C getDisplayName(T value) {
        return asDisplayName.apply(value);
    }

}
