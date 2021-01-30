package com.unascribed.blockrenderer.varia;

import com.unascribed.blockrenderer.varia.logging.Log;
import com.unascribed.blockrenderer.varia.logging.Markers;
import com.unascribed.blockrenderer.varia.rendering.STBWrapper;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public interface Files {

    File DEFAULT_FOLDER = new File("renders");

    static File getFile(File folder, String filename, String type) throws IOException {
        File file = new File(folder, filename + "." + type);
        createParentDirs(file);

        for (int i = 1; file.exists(); i++)
            file = new File(folder, filename + "_" + i + "." + type);

        return file;
    }

    static File getPng(File folder, String filename) throws IOException {
        return getFile(folder, filename, "png");
    }

    static File savePng(File file, STBWrapper image) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            image.write(fos);
        }
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

    static void createParentDirs(File file) throws IOException {
        File parent = file.getCanonicalFile().getParentFile();
        if (parent == null) return;
        //noinspection ResultOfMethodCallIgnored
        parent.mkdirs();
        if (!parent.isDirectory()) throw new IOException("Unable to create parent directories of " + file);
    }

    @FunctionalInterface
    interface IOSupplier<T> {
        T get() throws IOException;
    }

}
