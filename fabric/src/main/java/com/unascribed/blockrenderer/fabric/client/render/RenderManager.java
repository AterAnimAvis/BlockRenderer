package com.unascribed.blockrenderer.fabric.client.render;

import com.unascribed.blockrenderer.fabric.client.render.report.ProgressManager;
import com.unascribed.blockrenderer.render.IAnimatedRenderer;
import com.unascribed.blockrenderer.render.IRenderManager;
import com.unascribed.blockrenderer.render.IRenderer;
import com.unascribed.blockrenderer.render.IRequest;
import com.unascribed.blockrenderer.render.request.lambda.ImageHandler;
import com.unascribed.blockrenderer.varia.Files;
import com.unascribed.blockrenderer.varia.Images;
import com.unascribed.blockrenderer.varia.gif.GifWriter;
import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RenderManager implements IRenderManager {

    public static final IRenderManager INSTANCE = new RenderManager();

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

    private static final ITextComponent RENDERING_BULK = new StringTextComponent("Rendering Bulk").mergeStyle(TextFormatting.GOLD);

    @Override
    public <S, T> void bulk(IRenderer<S, T> renderer, ImageHandler<T> handler,
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
    private static final int MAX_CONSUME = FPS * AUTO_LOOP_LENGTH;

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

    private static final ITextComponent RENDERING_GIF = new StringTextComponent("Rendering GIF").mergeStyle(TextFormatting.GOLD);
    private static final ITextComponent RENDERING_AUTO = new StringTextComponent("Auto Loop").mergeStyle(TextFormatting.GOLD);
    private static final ITextComponent RENDERING_SKIP = new StringTextComponent("Skipping First").mergeStyle(TextFormatting.GOLD);

    private static <S, T> void animated(IAnimatedRenderer<S, T> renderer, Consumer<T> callback, S params, int length, boolean loop, T value, ImageHandler<T> write) throws RuntimeException {

        AtomicBoolean isSameAsInitial = new AtomicBoolean(true);
        AtomicReference<BufferedImage> initial = new AtomicReference<>();

        RenderManager.isRendering = true;
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
        ProgressManager.init(RENDERING_SKIP, -1);
        /* We need to handle the case where the target doesn't actually animate so we add a timeout */
        int timeout = MAX_CONSUME;
        while (isSameAsInitial.get() && timeout > 0) {
            timeout--;
            final ImageHandler<T> consumer = frame == 0 ? init : timeout == 0 ? write : writeDifferent;

            ProgressManager.push(ProgressManager.getProgress());
            renderer.render(value, consumer, NANOS_IN_A_SECOND / FPS * frame++);
            ProgressManager.pop();

            ProgressManager.render();
        }
        ProgressManager.end();

        /* Render for Specified Length */
        ProgressManager.init(RENDERING_GIF, length);
        ProgressManager.skip();

        for (int i = 1; i < length; i++) {
            final ImageHandler<T> consumer = i == length - 1 ? checkWrite : write;

            ProgressManager.push(ProgressManager.getProgress());
            renderer.render(value, consumer, NANOS_IN_A_SECOND / FPS * frame++);
            ProgressManager.pop();

            ProgressManager.render();

        }
        ProgressManager.end();

        if (loop) {
            /* Search for Loop Point */
            ProgressManager.init(RENDERING_AUTO, -1);
            for (int i = 0; i < FPS * AUTO_LOOP_LENGTH; i++) {
                ProgressManager.push(ProgressManager.getProgress());
                renderer.render(value, checkWrite, NANOS_IN_A_SECOND / FPS * frame++);
                ProgressManager.pop();

                ProgressManager.render();
                if (isSameAsInitial.get()) break;
            }
            /* Consume Additional Frames */
            timeout = MAX_CONSUME;
            while (isSameAsInitial.get() && timeout > 0) {
                timeout--;

                ProgressManager.push(ProgressManager.getProgress());
                renderer.render(value, writeSame, NANOS_IN_A_SECOND / FPS * frame++);
                ProgressManager.pop();

                ProgressManager.render();
            }
            ProgressManager.end();
        }

        callback.accept(value);

        renderer.teardown();
    }

}
