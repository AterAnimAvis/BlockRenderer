package com.unascribed.blockrenderer.varia.rendering;

import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import com.unascribed.blockrenderer.vendor.gif.api.IImage;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.function.IntConsumer;

public class STBWrapper implements IImage {

    private static final int RGBA_PIXEL_SIZE = 4;

    private final int[] buffer;

    private final int width;
    private final int height;

    public STBWrapper(int width, int height, ByteBuffer data) {
        this.width = width;
        this.height = height;

        buffer = new int[width * height];

        int[] pixels = new int[width * height];
        data.asIntBuffer().get(pixels);
        for (int y = 0; y < height; y++) {
            int ny = (height - 1) - y;
            System.arraycopy(pixels, y * width, buffer, ny * width, width);
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void forEach(IntConsumer consumer) {
        for (int c : buffer) {
            consumer.accept(c);
        }
    }

    public int getPixelRGBA(int x, int y) {
        return buffer[x + y * width];
    }

    private void setPixelRGBA(long pointer, int x, int y, int value) {
        long i = (x + (long) y * width) * RGBA_PIXEL_SIZE;
        MemoryUtil.memPutInt(pointer + i, value);
    }

    public void write(OutputStream os) throws IOException {
        long pointer = 0L;
        try {
            pointer = MemoryUtil.nmemAlloc((long) width * height * RGBA_PIXEL_SIZE);

            if (pointer == 0L) throw new IllegalStateException("Image is not allocated.");

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    setPixelRGBA(pointer, x, y, getPixelRGBA(x, y));
                }
            }

            try (ImageWriter callback = new ImageWriter(Channels.newChannel(os))) {
                int i = Math.min(height, Integer.MAX_VALUE / width / RGBA_PIXEL_SIZE);
                if (i < height)
                    Log.warn(Markers.STB, "Dropping image height from {} to {} to fit the size into 32-bit signed int", height, i);

                if (STBImageWrite.nstbi_write_png_to_func(callback.address(), 0L, width, i, RGBA_PIXEL_SIZE, pointer, 0) != 0) {
                    callback.propagateException();
                }
            }
        } finally {
            if (pointer != 0L) MemoryUtil.nmemFree(pointer);
        }
    }

    private static class ImageWriter extends STBIWriteCallback {

        private final WritableByteChannel channel;

        @Nullable
        private IOException exception = null;

        ImageWriter(WritableByteChannel channel) {
            this.channel = channel;
        }

        @Override
        public void invoke(long context, long data, int size) {
            ByteBuffer buffer = getData(data, size);
            try {
                channel.write(buffer);
            } catch (IOException e) {
                exception = e;
            }
        }

        public void propagateException() throws IOException {
            if (exception != null) throw exception;
        }

    }

}
