package com.unascribed.blockrenderer.fabric.client.render.item;

import java.io.File;

public class BulkPngItemStackHandler extends DefaultPngItemStackHandler implements Runnable {

    private final String name;

    public BulkPngItemStackHandler(String name, File folder, int size, boolean useIdentifier, boolean addSize, boolean addDate) {
        super(folder, size, useIdentifier, addSize, addDate);
        this.name = name;
    }

    @Override
    public void run() {
        report(name);
    }

}
