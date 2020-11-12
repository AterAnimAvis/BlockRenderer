package com.unascribed.blockrenderer.fabric.client.render;

import com.unascribed.blockrenderer.fabric.client.render.report.ProgressManager;
import com.unascribed.blockrenderer.render.IAnimatedRenderer;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.render.IRequest;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.Images;
import com.unascribed.blockrenderer.varia.gif.GifWriter;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class RenderManager {

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

    public static <S, T> void render(IRenderer<S, T> renderer, ImageHandler<T> handler,
                                     S params, T value, Consumer<T> callback) {
        isRendering = true;

        renderer.setup(params);
        renderer.render(value, handler);
        renderer.teardown();
        callback.accept(value);

        isRendering = false;
    }

    private static final Text RENDERING_BULK = new LiteralText("Rendering Bulk").formatted(Formatting.GOLD);

    public static <S, T> void bulk(IRenderer<S, T> renderer, ImageHandler<T> handler,
                                   S params, Collection<T> values) {
        isRendering = true;

        renderer.setup(params);

        ProgressManager.init(RENDERING_BULK, values.size());
        for (T value : values) {
            ProgressManager.push(ProgressManager.getProgress());
            renderer.render(value, handler);
            ProgressManager.pop();

            ProgressManager.render();
        }
        ProgressManager.end();

        renderer.teardown();

        isRendering = false;
    }

    private static final int FPS = 20;
    private static final int AUTO_LOOP_LENGTH = 30;
    private static final long NANOS_IN_A_SECOND = 1_000_000_000L;

    public static <S, T> void animated(IAnimatedRenderer<S, T> renderer,
                                       Function<T, ImageOutputStream> provider,
                                       Consumer<T> callback,
                                       S params,
                                       int length,
                                       boolean loop,
                                       T value) {
        try {
            try (ImageOutputStream stream = provider.apply(value)) {
                if (stream == null) return;

                try (GifWriter writer = new GifWriter(stream, FPS, true)) {
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
            ProgressManager.end();
            isRendering = false;
        }
    }

    private static final Text RENDERING_GIF = new LiteralText("Rendering GIF").formatted(Formatting.GOLD);
    private static final Text RENDERING_AUTO = new LiteralText("Auto Loop").formatted(Formatting.GOLD);

    private static <S, T> void animated(IAnimatedRenderer<S, T> renderer, Consumer<T> callback, S params, int length, boolean loop, T value, ImageHandler<T> write) throws RuntimeException {

        AtomicBoolean isSameAsInitial = new AtomicBoolean(false);
        AtomicReference<BufferedImage> initial = new AtomicReference<>();

        RenderManager.isRendering = true;

        renderer.setup(params);

        ImageHandler<T> init = (v, image) -> initial.compareAndSet(null, image);
        ImageHandler<T> checkImage = (v, image) -> isSameAsInitial.set(Images.same(initial.get(), image));
        ImageHandler<T> writeFinal = checkImage.andThen((v, image) -> {
            if (!isSameAsInitial.get()) write.accept(v, image);
        });

        /* Render for Specified Length */
        ProgressManager.init(RENDERING_GIF, length);
        for (int i = 0; i < length; i++) {
            final ImageHandler<T> consumer = i == 0 ? init.andThen(write) : i == length - 1 ? checkImage.andThen(write) : write;

            ProgressManager.push(ProgressManager.getProgress());
            renderer.render(value, consumer, NANOS_IN_A_SECOND / FPS * i);
            ProgressManager.pop();

            ProgressManager.render();

        }
        ProgressManager.end();

        if (loop) {
            /* Search for Loop Point */
            ProgressManager.init(RENDERING_AUTO, -1);
            for (int i = 0; i < FPS * AUTO_LOOP_LENGTH; i++) {
                ProgressManager.push(ProgressManager.getProgress());
                renderer.render(value, writeFinal, NANOS_IN_A_SECOND / FPS * (length + i));
                ProgressManager.pop();

                if (isSameAsInitial.get()) break;
                ProgressManager.render();
            }
            ProgressManager.end();
        }

        callback.accept(value);

        renderer.teardown();
    }

}
