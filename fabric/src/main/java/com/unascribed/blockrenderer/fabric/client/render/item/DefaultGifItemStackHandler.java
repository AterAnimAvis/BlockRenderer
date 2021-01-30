package com.unascribed.blockrenderer.fabric.client.render.item;

import com.unascribed.blockrenderer.varia.Files;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.function.Function;

public class DefaultGifItemStackHandler extends BaseItemStackHandler implements Function<ItemStack, OutputStream> {

    public DefaultGifItemStackHandler(File folder, int size, boolean useIdentifier, boolean addSize, boolean addDate) {
        super(folder, size, useIdentifier, addSize, addDate);
    }

    @Override
    @Nullable
    public OutputStream apply(ItemStack value) {
        future = Files.wrap("Exception whilst generating gif", () -> Files.getGif(folder, getFilename(value)));

        if (future == null) return null;

        return Files.wrap("Exception whilst generating gif", () -> new FileOutputStream(future));
    }

    public void acceptZip(File file) {
        future = file;
    }

}
