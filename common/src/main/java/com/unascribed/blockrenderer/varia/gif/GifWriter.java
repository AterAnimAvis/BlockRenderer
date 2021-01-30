package com.unascribed.blockrenderer.varia.gif;

import com.unascribed.blockrenderer.vendor.gif.GIF;
import com.unascribed.blockrenderer.vendor.gif.api.DisposalMethod;
import com.unascribed.blockrenderer.vendor.gif.api.IGifExtendedImageOptions;
import com.unascribed.blockrenderer.vendor.gif.api.IImage;
import com.unascribed.blockrenderer.vendor.gif.api.IIndexedColorImage;
import com.unascribed.blockrenderer.vendor.gif.impl.BufferedImageAdapter;
import com.unascribed.blockrenderer.vendor.gif.impl.GifExtendedImageOptions;
import com.unascribed.blockrenderer.vendor.gif.impl.IndexedColorImage;
import com.unascribed.blockrenderer.vendor.gif.indexed.MedianCutColorReducer;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public class GifWriter implements Closeable {

    private final OutputStream os;
    private final IGifExtendedImageOptions options;
    private final int repeats;

    private boolean first = true;

    public GifWriter(OutputStream os, int delayInMS, boolean repeats) throws IOException {
        this.os = os;
        this.repeats = repeats ? 0x00 : 0x01;
        this.options = new GifExtendedImageOptions(0, 0, delayInMS, DisposalMethod.RESTORE_TO_BACKGROUND, 0);

        GIF.writeHeader(os);
    }

    public void writeFrame(BufferedImage image) throws IOException {
        writeFrame(new BufferedImageAdapter(image));
    }

    public void writeFrame(IImage image) throws IOException {
        MedianCutColorReducer reducer = new MedianCutColorReducer(image, 0xFF - 1);

        writeFrame(new IndexedColorImage(image.getWidth(), image.getHeight(), reducer.remap(image), reducer.paletteData));
    }

    public void writeFrame(IIndexedColorImage image) throws IOException {
        if (first) setup(image);

        GIF.writeTableBasedImageWithGraphicControl(os, image, options);
    }

    private void setup(IIndexedColorImage image) throws IOException {
        first = false;

        GIF.writeLogicalScreenInfo(os, image.getWidth(), image.getHeight());
        GIF.writeLoopControl(os, repeats);
    }

    @Override
    public void close() throws IOException {
        GIF.writeTrailer(os);
    }

}
