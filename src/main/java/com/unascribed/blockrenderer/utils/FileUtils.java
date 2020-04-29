package com.unascribed.blockrenderer.utils;

import java.io.File;

public interface FileUtils {

    static File getFile(File folder, String filename) {
        File file = new File(folder, filename+".png");

        int i = 2;
        while (file.exists()) {
            file = new File(folder, filename+"_"+i+".png");
            i++;
        }

        return file;
    }

}
