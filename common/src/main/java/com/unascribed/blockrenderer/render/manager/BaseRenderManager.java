package com.unascribed.blockrenderer.render.manager;

import com.unascribed.blockrenderer.render.IAnimatedRenderer;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.render.report.BaseReporter;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.Files;
import com.unascribed.blockrenderer.varia.Images;
import com.unascribed.blockrenderer.varia.Time;
import com.unascribed.blockrenderer.varia.gif.GifWriter;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class BaseRenderManager<Component> implements IRenderManager {

    BaseReporter<Component> reporter;

    final Function<String, Component> RENDERING_BULK;
    final Component RENDERING_GIF;
    final Component RENDERING_AUTO;
    final Component RENDERING_SKIP;

    protected BaseRenderManager(BaseReporter<Component> reporter,
                                Function<String, Component> renderingBulk,
                                Component renderingGIF,
                                Component renderingAuto,
                                Component renderingSkip) {
        this.reporter = reporter;
        RENDERING_BULK = renderingBulk;
        RENDERING_GIF = renderingGIF;
        RENDERING_AUTO = renderingAuto;
        RENDERING_SKIP = renderingSkip;
    }

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
    public <S, T> void bulk(IRenderer<S, T> renderer, ImageHandler<T> handler, String name, S params, Collection<T> values) {
        isRendering = true;

        renderer.setup(params);

        reporter.init(RENDERING_BULK.apply(name), values.size());
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
                                boolean zip,
                                String zipFile,
                                Consumer<File> zipFileCallback,
                                T value) {
        try {
            if (zip) {
                File output = Files.getFile(Files.DEFAULT_FOLDER, zipFile, "zip");
                zipFileCallback.accept(output);

                AtomicInteger frame = new AtomicInteger(0);
                try (ZipOutputStream stream = new ZipOutputStream(new FileOutputStream(output))) {
                    ImageHandler<T> writeFrame = (v, img) -> {
                        try {
                            stream.putNextEntry(new ZipEntry(String.format("%04d.png", frame.getAndIncrement())));
                            ImageIO.write(img, "PNG", stream);
                            stream.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    };

                    animated(renderer, callback, params, length, loop, value, writeFrame);
                }
                return;
            }

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

        AtomicBoolean isSameAsInitial = new AtomicBoolean(true);
        AtomicReference<BufferedImage> initial = new AtomicReference<>();

        isRendering = true;
        int frame = 0;

        renderer.setup(params);

        ImageHandler<T> init = (v, image) -> initial.compareAndSet(null, image);
        ImageHandler<T> checkImage = (v, image) -> isSameAsInitial.set(Images.same(initial.get(), image));
        ImageHandler<T> checkWrite = checkImage.andThen(write);
        ImageHandler<T> writeDifferent = checkImage.andThen((v, image) -> {
            if (!isSameAsInitial.get()) write.accept(v, image);
        });
        ImageHandler<T> writeSame = checkImage.andThen((v, image) -> {
            if (isSameAsInitial.get()) write.accept(v, image);
        });

        /* Skip First Frame + Render Second */
        reporter.init(RENDERING_SKIP, -1);
        /* We need to handle the case where the target doesn't actually animate so we add a timeout */
        int timeout = Time.MAX_CONSUME;
        while (isSameAsInitial.get() && timeout > 0) {
            timeout--;
            final ImageHandler<T> consumer = frame == 0 ? init : timeout == 0 ? write : writeDifferent;

            reporter.push(reporter.getProgress());
            renderer.render(value, consumer, Time.NANOS_PER_FRAME * frame++);
            reporter.pop();

            reporter.render();
        }
        reporter.end();

        /* Render for Specified Length */
        reporter.init(RENDERING_GIF, length);
        reporter.skip();

        for (int i = 1; i < length; i++) {
            final ImageHandler<T> consumer = i == length - 1 ? checkWrite : write;

            reporter.push(reporter.getProgress());
            renderer.render(value, consumer, Time.NANOS_PER_FRAME * frame++);
            reporter.pop();

            reporter.render();

        }
        reporter.end();

        if (loop) {
            /* Search for Loop Point */
            reporter.init(RENDERING_AUTO, -1);
            for (int i = 0; i < Time.AUTO_LOOP; i++) {
                reporter.push(reporter.getProgress());
                renderer.render(value, checkWrite, Time.NANOS_PER_FRAME * frame++);
                reporter.pop();

                reporter.render();
                if (isSameAsInitial.get()) break;
            }
            /* Consume Additional Frames */
            timeout = Time.MAX_CONSUME;
            while (isSameAsInitial.get() && timeout > 0) {
                timeout--;

                reporter.push(reporter.getProgress());
                renderer.render(value, writeSame, Time.NANOS_PER_FRAME * frame++);
                reporter.pop();

                reporter.render();
            }
            reporter.end();
        }

        callback.accept(value);

        renderer.teardown();
    }

}
