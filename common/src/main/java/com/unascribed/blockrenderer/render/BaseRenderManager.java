package com.unascribed.blockrenderer.render;

import com.unascribed.blockrenderer.render.report.BaseReporter;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.Images;
import com.unascribed.blockrenderer.varia.Time;
import com.unascribed.blockrenderer.varia.gif.GifWriter;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;

import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BaseRenderManager<Component> implements IRenderManager {

    BaseReporter<Component> reporter;

    final Component RENDERING_GIF;
    final Component RENDERING_AUTO;

    protected BaseRenderManager(BaseReporter<Component> reporter,
                                Component renderingGIF,
                                Component renderingAuto) {
        this.reporter = reporter;
        RENDERING_GIF = renderingGIF;
        RENDERING_AUTO = renderingAuto;
    }

    protected abstract Component renderingBulk(String name);

    private static final int AUTO_LOOP_LENGTH = 30;

    public boolean isRendering = false;

    @Override
    public <S, T> void render(IRenderer<S, T> renderer, ImageHandler<T> handler,
                              S params, T value, Consumer<T> callback) {
        isRendering = true;

        renderer.setup(params);
        renderer.render(value, handler);
        renderer.teardown();
        callback.accept(value);

        isRendering = false;
    }

    @Override
    public <S, T> void bulk(IRenderer<S, T> renderer, ImageHandler<T> handler,
                            S params, String name, Collection<T> values) {
        isRendering = true;

        renderer.setup(params);

        reporter.init(renderingBulk(name), values.size());
        for (T value : values) {
            reporter.push(reporter.getProgress());
            renderer.render(value, handler);
            reporter.pop();

            reporter.render();
        }
        reporter.end();

        renderer.teardown();

        isRendering = false;
    }

    @Override
    public <S, T> void animated(IAnimatedRenderer<S, T> renderer,
                                Function<T, ImageOutputStream> provider,
                                Consumer<T> callback,
                                S params,
                                int length,
                                boolean loop,
                                T value) {
        try {
            try (ImageOutputStream stream = provider.apply(value)) {
                if (stream == null) return;

                try (GifWriter writer = new GifWriter(stream, Time.TICKS_IN_A_SECOND, true)) {
                    ImageHandler<T> writeFrame = (v, img) -> {
                        try {
                            writer.writeFrame(img);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    };

                    animated(renderer, callback, params, length, loop, value, writeFrame);
                }
            }
        } catch (Exception e) {
            Log.error(Markers.MANAGER, "Exception", e);
        } finally {
            reporter.end();
            isRendering = false;
        }
    }

    private <S, T> void animated(IAnimatedRenderer<S, T> renderer, Consumer<T> callback, S params, int length, boolean loop, T value, ImageHandler<T> write) throws RuntimeException {

        AtomicBoolean isSameAsInitial = new AtomicBoolean(false);
        AtomicReference<BufferedImage> initial = new AtomicReference<>();

        isRendering = true;

        try {
            renderer.setup(params);

            ImageHandler<T> init = (v, image) -> initial.compareAndSet(null, image);
            ImageHandler<T> checkImage = (v, image) -> isSameAsInitial.set(Images.same(initial.get(), image));
            ImageHandler<T> writeFinal = checkImage.andThen((v, image) -> {
                if (!isSameAsInitial.get()) write.accept(v, image);
            });

            /* Render for Specified Length */
            reporter.init(RENDERING_GIF, length);
            for (int i = 0; i < length; i++) {
                final ImageHandler<T> consumer = i == 0 ? init.andThen(write) : i == length - 1 ? checkImage.andThen(write) : write;

                reporter.push(reporter.getProgress());
                renderer.render(value, consumer, Time.NANOS_PER_FRAME * i);
                reporter.pop();

                reporter.render();

            }
            reporter.end();

            if (loop) {
                /* Search for Loop Point */
                reporter.init(RENDERING_AUTO, -1);
                for (int i = 0; i < Time.TICKS_IN_A_SECOND * AUTO_LOOP_LENGTH; i++) {
                    reporter.push(reporter.getProgress());
                    renderer.render(value, writeFinal, Time.NANOS_PER_FRAME * (length + i));
                    reporter.pop();

                    if (isSameAsInitial.get()) break;
                    reporter.render();
                }
                reporter.end();
            }

            callback.accept(value);
        } finally {
            renderer.teardown();
        }
    }

}
