package com.unascribed.blockrenderer.client.varia;

import com.unascribed.blockrenderer.client.varia.logging.Log;
import com.unascribed.blockrenderer.client.varia.logging.Markers;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface Files {

    File DEFAULT_FOLDER = new File("renders");

    @SuppressWarnings("UnstableApiUsage")
    static File getFile(File folder, String filename, String type) throws IOException {
        File file = new File(folder, filename + "." + type);
        com.google.common.io.Files.createParentDirs(file);

        for (int i = 1; file.exists(); i++)
            file = new File(folder, filename + "_" + i + "." + type);

        return file;
    }

    static File getPng(File folder, String filename) throws IOException {
        return getFile(folder, filename, "png");
    }

    static File savePng(File file, BufferedImage image) throws IOException {
        ImageIO.write(image, "PNG", file);
        return file;
    }

    static File getGif(File folder, String filename) throws IOException {
        return getFile(folder, filename, "gif");
    }

    @Nullable
    static <T> T wrap(String message, IOSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (IOException e) {
            Log.error(Markers.FILE, message, e);
        }

        return null;
    }

    @FunctionalInterface
    interface IOSupplier<T> {
        T get() throws IOException;
    }

}
