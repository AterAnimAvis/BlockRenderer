package com.unascribed.blockrenderer.varia.stream;

import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageOutputStreamWrapper extends OutputStream {

    private final ImageOutputStream os;

    public ImageOutputStreamWrapper(ImageOutputStream os) {
        this.os = os;
    }

    @Override
    public void write(byte[] b) throws IOException {
        os.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        os.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        os.flush();
    }

    @Override
    public void close() throws IOException {
        os.close();
    }

    @Override
    public void write(int b) throws IOException {
        os.write(b);
    }

}
